package org.synyx.urlaubsverwaltung.web.person;

import org.apache.log4j.Logger;

import org.joda.time.DateMidnight;
import org.joda.time.chrono.GregorianChronology;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.synyx.urlaubsverwaltung.core.account.Account;
import org.synyx.urlaubsverwaltung.core.account.AccountService;
import org.synyx.urlaubsverwaltung.core.application.domain.Application;
import org.synyx.urlaubsverwaltung.core.application.domain.ApplicationStatus;
import org.synyx.urlaubsverwaltung.core.application.domain.DayLength;
import org.synyx.urlaubsverwaltung.core.application.service.ApplicationService;
import org.synyx.urlaubsverwaltung.core.application.service.CalculationService;
import org.synyx.urlaubsverwaltung.core.calendar.OwnCalendarService;
import org.synyx.urlaubsverwaltung.core.person.Person;
import org.synyx.urlaubsverwaltung.core.person.PersonService;
import org.synyx.urlaubsverwaltung.core.sicknote.SickNote;
import org.synyx.urlaubsverwaltung.core.sicknote.SickNoteService;
import org.synyx.urlaubsverwaltung.core.sicknote.SickNoteType;
import org.synyx.urlaubsverwaltung.core.util.DateUtil;
import org.synyx.urlaubsverwaltung.security.SessionService;
import org.synyx.urlaubsverwaltung.web.ControllerConstants;
import org.synyx.urlaubsverwaltung.web.application.UsedDaysOverview;
import org.synyx.urlaubsverwaltung.web.util.GravatarUtil;

import java.math.BigDecimal;

import java.util.*;


/**
 * Controller for the different ways of displaying the personal overview page.
 *
 * @author  Aljona Murygina - murygina@synyx.de
 */
@Controller
public class PersonalOverviewController {

    private static final Logger LOG = Logger.getLogger(PersonalOverviewController.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OwnCalendarService calendarService;

    @Autowired
    private SickNoteService sickNoteService;

    /**
     * Default personal overview for user: information about one's leave accounts, entitlement of holidays, list of
     * applications, etc. If there is no parameter set for year, take current year for view.
     *
     * @param  year
     * @param  model
     *
     * @return
     */
    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    public String showPersonalOverview(@RequestParam(value = ControllerConstants.YEAR, required = false) String year,
        Model model) {

        if (sessionService.isInactive()) {
            return ControllerConstants.LOGIN_LINK;
        } else {
            Person person = sessionService.getLoggedUser();
            prepareOverview(person, parseYearParameter(year), model);
            model.addAttribute(PersonConstants.LOGGED_USER, sessionService.getLoggedUser());

            return PersonConstants.OVERVIEW_JSP;
        }
    }


    /**
     * The office is able to see overviews of staff with information about this person's leave accounts, entitlement of
     * holidays, list of applications, etc. The default overview of a staff member (no parameter set for year) is for
     * the current year.
     *
     * @param  personId
     * @param  year
     * @param  model
     *
     * @return
     */
    @RequestMapping(value = "/staff/{" + PersonConstants.PERSON_ID + "}/overview", method = RequestMethod.GET)
    public String showStaffOverview(@PathVariable(PersonConstants.PERSON_ID) Integer personId,
        @RequestParam(value = ControllerConstants.YEAR, required = false) String year, Model model) {

        if (sessionService.isOffice() || sessionService.isBoss()) {
            Person person = personService.getPersonByID(personId);
            List<Person> persons;

            if (person.isActive()) {
                persons = personService.getActivePersons();
            } else {
                persons = personService.getInactivePersons();
            }

            model.addAttribute(ControllerConstants.PERSONS, persons);

            prepareOverview(person, parseYearParameter(year), model);

            model.addAttribute(PersonConstants.LOGGED_USER, sessionService.getLoggedUser());

            return PersonConstants.OVERVIEW_JSP;
        }

        return ControllerConstants.ERROR_JSP;
    }


    /**
     * Parses the year of the given String, if parsing fails, the current year is returned.
     *
     * @param  input
     *
     * @return  parsed Integer of the given String, if parsing failed the current year is returned
     */
    private Integer parseYearParameter(String input) {

        // default value for year is the current year
        Integer year = DateMidnight.now(GregorianChronology.getInstance()).getYear();

        if (StringUtils.hasText(input)) {
            try {
                year = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                LOG.warn("Tried to show overview for an invalid year entry: " + input, ex);
            }
        }

        return year;
    }


    /**
     * Prepares model for overview with given person and year.
     *
     * @param  person
     * @param  year
     * @param  model
     */
    private void prepareOverview(Person person, int year, Model model) {

        prepareApplications(person, year, model);

        prepareHolidayAccounts(person, year, model);

        model.addAttribute(PersonConstants.LOGGED_USER, sessionService.getLoggedUser());
        model.addAttribute(ControllerConstants.PERSON, person);
        model.addAttribute(ControllerConstants.YEAR, DateMidnight.now().getYear());

        String url = GravatarUtil.createImgURL(person.getEmail());
        model.addAttribute(PersonConstants.GRAVATAR, url);

        prepareSickNoteList(person, year, model);
    }


    private void prepareSickNoteList(Person person, int year, Model model) {

        List<SickNote> sickNotes = sickNoteService.getByPersonAndPeriod(person, DateUtil.getFirstDayOfYear(year),
                DateUtil.getLastDayOfYear(year));

        BigDecimal sickDays = BigDecimal.ZERO;
        BigDecimal sickDaysWithAUB = BigDecimal.ZERO;
        BigDecimal childSickDays = BigDecimal.ZERO;
        BigDecimal childSickDaysWithAUB = BigDecimal.ZERO;

        for (SickNote sickNote : sickNotes) {
            if (sickNote.getType().equals(SickNoteType.SICK_NOTE_CHILD)) {
                childSickDays = childSickDays.add(sickNote.getWorkDays());

                if (sickNote.isAubPresent()) {
                    BigDecimal workDays = calendarService.getWorkDays(DayLength.FULL, sickNote.getAubStartDate(),
                            sickNote.getAubEndDate(), person);
                    childSickDaysWithAUB = childSickDaysWithAUB.add(workDays);
                }
            } else {
                sickDays = sickDays.add(sickNote.getWorkDays());

                if (sickNote.isAubPresent()) {
                    BigDecimal workDays = calendarService.getWorkDays(DayLength.FULL, sickNote.getAubStartDate(),
                            sickNote.getAubEndDate(), person);
                    sickDaysWithAUB = sickDaysWithAUB.add(workDays);
                }
            }
        }

        Collections.sort(sickNotes, new Comparator<SickNote>() {

                @Override
                public int compare(SickNote o1, SickNote o2) {

                    // show latest sick notes at first
                    return o2.getStartDate().compareTo(o1.getStartDate());
                }
            });

        model.addAttribute("sickDays", sickDays);
        model.addAttribute("sickDaysWithAUB", sickDaysWithAUB);
        model.addAttribute("childSickDays", childSickDays);
        model.addAttribute("childSickDaysWithAUB", childSickDaysWithAUB);
        model.addAttribute("sickNotes", sickNotes);
    }


    private void prepareApplications(Person person, int year, Model model) {

        // get the person's applications for the given year
        List<Application> apps = applicationService.getApplicationsForACertainPeriodAndPerson(DateUtil
                .getFirstDayOfYear(year), DateUtil.getLastDayOfYear(year), person);

        if (!apps.isEmpty()) {
            List<Application> applications = new ArrayList<Application>();

            for (Application a : apps) {
                if ((a.getStatus() != ApplicationStatus.CANCELLED)
                        || (a.getStatus() == ApplicationStatus.CANCELLED && a.isFormerlyAllowed())) {
                    applications.add(a);
                }
            }

            Collections.sort(applications, new Comparator<Application>() {

                    @Override
                    public int compare(Application o1, Application o2) {

                        // show latest applications at first
                        return o2.getStartDate().compareTo(o1.getStartDate());
                    }
                });

            model.addAttribute(ControllerConstants.APPLICATIONS, applications);

            UsedDaysOverview usedDaysOverview = new UsedDaysOverview(applications, year, calendarService);
            model.addAttribute("usedDaysOverview", usedDaysOverview);
        }
    }


    private void prepareHolidayAccounts(Person person, int year, Model model) {

        // get person's holidays account and entitlement for the given year
        Account account = accountService.getHolidaysAccount(year, person);

        if (account != null) {
            BigDecimal vacationDaysLeft = calculationService.calculateLeftVacationDays(account);
            BigDecimal remainingVacationDaysLeft = calculationService.calculateLeftRemainingVacationDays(account);
            model.addAttribute(PersonConstants.LEFT_DAYS, vacationDaysLeft);
            model.addAttribute(PersonConstants.REM_LEFT_DAYS, remainingVacationDaysLeft);
            model.addAttribute(PersonConstants.BEFORE_APRIL, DateUtil.isBeforeApril(DateMidnight.now()));
        }

        model.addAttribute(ControllerConstants.ACCOUNT, account);
    }
}
