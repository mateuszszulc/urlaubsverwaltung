/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synyx.urlaubsverwaltung.core.mail;

import org.synyx.urlaubsverwaltung.core.account.Account;
import org.synyx.urlaubsverwaltung.core.application.domain.Application;
import org.synyx.urlaubsverwaltung.core.application.domain.Comment;
import org.synyx.urlaubsverwaltung.core.person.Person;
import org.synyx.urlaubsverwaltung.core.sicknote.SickNote;

import java.util.List;


/**
 * This service provides sending notification emails.
 *
 * @author  Johannes Reuter
 * @author  Aljona Murygina
 */

public interface MailService {

    /**
     * sends an email to the applicant that the application has been made successfully.
     *
     * @param  application
     */
    void sendConfirmation(Application application);


    /**
     * sends an email to the person of the given application that the office has applied for leave on behalf of himself.
     *
     * @param  application
     */
    void sendAppliedForLeaveByOfficeNotification(Application application);


    /**
     * sends an email to the bosses notifying that there is a new application for leave which has to be allowed or
     * rejected by a boss.
     *
     * @param  application
     */
    void sendNewApplicationNotification(Application application);


    /**
     * sends an email to the applicant and to the office that the application has been allowed.
     *
     * @param  application  the application which got allowed
     * @param  comment  made during allowing application
     */
    void sendAllowedNotification(Application application, Comment comment);


    /**
     * sends an email to the applicant that the application has been rejected.
     *
     * @param  application  the application which got rejected
     * @param  comment  reason why application was rejected
     */
    void sendRejectedNotification(Application application, Comment comment);


    /**
     * If a boss is not sure about the decision of an application (reject or allow), he can ask another boss to decide
     * about this application via a generated email.
     *
     * @param  application
     * @param  recipient
     * @param  sender
     */
    void sendReferApplicationNotification(Application application, Person recipient, Person sender);


    /**
     * If an application has status waiting and no boss has decided about it after a certain time, the bosses receive a
     * reminding notification.
     *
     * @param  application
     */
    void sendRemindBossNotification(Application application);


    /**
     * This method sends an email if an application got cancelled. If the application had the status allowed and was
     * cancelled by the applicant, the office gets an email. If the application was cancelled by the office (regardless
     * of which status), the applicant gets an email.
     *
     * @param  application  the application which got canceled
     * @param  cancelledByOffice  describes if chefs (param is true) or office (param is false) get the email (dependent
     *                            on application's state: waiting-chefs, allowed-office
     * @param  comment
     */
    void sendCancelledNotification(Application application, boolean cancelledByOffice, Comment comment);


    /**
     * this method sends an email to the tool's manager to inform if an error occurred while generating private and
     * public key for a new user with the given login name.
     *
     * @param  loginName
     * @param  exception
     */
    void sendKeyGeneratingErrorNotification(String loginName, String exception);


    /**
     * this method sends an email to the tool's manager to inform if an error occurred while signing an application with
     * the given id.
     *
     * @param  applicationId
     * @param  exception
     */
    void sendSignErrorNotification(Integer applicationId, String exception);


    /**
     * Sends mail to the tool's manager if holidays accounts were updated successfully on 1st January of a year.
     * (setting remaining vacation days)
     *
     * @param  updatedAccounts  that have been successfully updated
     */
    void sendSuccessfullyUpdatedAccounts(List<Account> updatedAccounts);


    /**
     * Sends mail to the affected person if sick note is converted to vacation.
     *
     * @param  application
     */
    void sendSickNoteConvertedToVacationNotification(Application application);


    /**
     * Sends mail to person and office if sick pay (gesetzliche Lohnfortzahlung im Krankheitsfall) is about to end.
     *
     * @param  sickNote
     */
    void sendEndOfSickPayNotification(SickNote sickNote);


    /**
     * Sends mail to person to inform that he/she has been selected as representative during someone's vacation.
     *
     * @param  application
     */
    void notifyRepresentative(Application application);
}
