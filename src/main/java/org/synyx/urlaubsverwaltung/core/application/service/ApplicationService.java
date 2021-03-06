package org.synyx.urlaubsverwaltung.core.application.service;

import org.joda.time.DateMidnight;

import org.synyx.urlaubsverwaltung.core.application.domain.Application;
import org.synyx.urlaubsverwaltung.core.application.domain.ApplicationStatus;
import org.synyx.urlaubsverwaltung.core.person.Person;

import java.util.List;


/**
 * This service provides read-only access to the {@link Application}s for leave. Interactions occur in
 * {@link org.synyx.urlaubsverwaltung.core.application.service.ApplicationInteractionService}.
 *
 * @author  Aljona Murygina - murygina@synyx.de
 */
public interface ApplicationService {

    /**
     * Returns the Id of the latest {@link Application} for the given {@link Person} and {@link ApplicationStatus}.
     *
     * @param  person {@link Person}
     * @param  status {@link ApplicationStatus}
     *
     * @return  int Id (primary key of {@link Application})
     */
    int getIdOfLatestApplication(Person person, ApplicationStatus status);


    /**
     * Gets an {@link Application} by its primary key.
     *
     * @param  id  Integer
     *
     * @return  {@link Application} for the given id
     */
    Application getApplicationById(Integer id);


    /**
     * Saves a new {@link Application}.
     *
     * @param  application {@link Application} the application to be saved
     */
    void save(Application application);


    /**
     * Gets all {@link Application}s with vacation time between startDate x and endDate y.
     *
     * @param  startDate {@link DateMidnight}
     * @param  endDate {@link DateMidnight}
     *
     * @return  all {@link Application}s with vacation time between startDate x and endDate y
     */
    List<Application> getApplicationsForACertainPeriod(DateMidnight startDate, DateMidnight endDate);


    /**
     * Gets all {@link Application}s with vacation time between startDate x and endDate y for the given person.
     *
     * @param  startDate {@link DateMidnight}
     * @param  endDate {@link DateMidnight}
     * @param  person {@link Person}
     *
     * @return  all {@link Application}s of the given person with vacation time between startDate x and endDate y
     */
    List<Application> getApplicationsForACertainPeriodAndPerson(DateMidnight startDate, DateMidnight endDate,
        Person person);


    /**
     * Gets all {@link Application}s with vacation time between startDate x and endDate y for the given state.
     *
     * @param  startDate {@link DateMidnight}
     * @param  endDate {@link DateMidnight}
     * @param  status {@link org.synyx.urlaubsverwaltung.core.application.domain.ApplicationStatus}
     *
     * @return  all {@link Application}s with the given state and vacation time between startDate x and endDate y
     */
    List<Application> getApplicationsForACertainPeriodAndState(DateMidnight startDate, DateMidnight endDate,
        ApplicationStatus status);


    /**
     * Gets all {@link Application}s with vacation time between startDate x and endDate y for the given person and
     * state.
     *
     * @param  startDate {@link DateMidnight}
     * @param  endDate {@link DateMidnight}
     * @param  person {@link Person}
     * @param  status {@link org.synyx.urlaubsverwaltung.core.application.domain.ApplicationStatus}
     *
     * @return  all {@link Application}s of the given person with vacation time between startDate x and endDate y and
     *          with a certain state
     */
    List<Application> getApplicationsForACertainPeriodAndPersonAndState(DateMidnight startDate, DateMidnight endDate,
        Person person, ApplicationStatus status);
}
