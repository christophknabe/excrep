package lg;

import java.util.Date;
import java.util.List;

import lg.Client.EmptyPhoneNumberExc;
import lg.Client.IncredibleBirthDateExc;
import lg.Client.PhoneNumberFormatExc;
import multex.Exc;

/** Session giving access to the business logic of the client management software. */
public interface Session {

	public Client createClient(
		String firstName, String lastName, Date birthDate, String phone
	) throws PhoneNumberFormatExc, IncredibleBirthDateExc, EmptyPhoneNumberExc;

	public void delete(final Client client);

	public Client findClient(final long id) throws Exc;

	public List<Client> searchAllClients() throws Exc;

	public void commit();

}