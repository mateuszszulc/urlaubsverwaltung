package org.synyx.urlaubsverwaltung.service;

import java.util.List;

import org.synyx.urlaubsverwaltung.domain.Person;

/**
 * 
 * use this service to access to the person-data (firstname, email,
 * vacation-days per year, etc. etc.)
 * 
 * @author johannes
 * 
 */
public interface PersonService {

	/**
	 * use this to save resp. edit someones profile
	 * 
	 * @param person
	 *            the data to save
	 */
	void save(Person person);

	/**
	 * use this to delete someones profile
	 * 
	 * @param person
	 *            the profile to delete
	 */
	void delete(Person person);

	/**
	 * finds a person in the database by his/her id
	 * 
	 * @param id
	 *            the id of the person
	 * @return returns the profile as a Person-object
	 */
	Person getPersonByID(Integer id);

	/**
	 * returns all profiles in the database as a list of person-objects
	 * 
	 * @return returns all profiles in the database as a list of person-objects
	 */
	List<Person> getAllPersons();

}