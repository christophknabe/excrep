package lg;

import java.util.Date;
import java.util.List;

import lg.Client.EmptyPhoneNumberExc;
import lg.Client.IncredibleBirthDateExc;
import lg.Client.PhoneNumberFormatExc;
import multex.Exc;

/** Session giving access to the business logic of the client management software. */
public interface Session {

	Client createClient(
		String firstName, String lastName, Date birthDate, String phone
	) throws PhoneNumberFormatExc, IncredibleBirthDateExc, EmptyPhoneNumberExc;

	void delete(final Client client);

	Client findClient(final long id) throws Exc;

	List<Client> searchAllClients() throws Exc;

	void commit();

}
