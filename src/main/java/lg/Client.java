package lg;

import java.io.Serializable;
import java.util.Date;

import static multex.MultexUtil.create;

/**A Client to be managed*/
public class Client implements Serializable {
	
	
	private static final long serialVersionUID = 6240957601030194430L;
	private static final Date beginDate = new Date(0, 0, 1); //01.01.1900
	private static final String ALLOWED_PHONE_CHARS = "0123456789 ()/+-";
	private static final String ALLOWED_PHONE_PATTERN = "[" + ALLOWED_PHONE_CHARS + "]*";

	private final long id;
	private String firstName;
	private String lastName;
	private Date   birthDate;
	private String phone;	
	
	
	/*package*/ Client(final long id, final String firstName, final String lastName, final Date birthDate, final String phone) throws IncredibleBirthDateExc, EmptyPhoneNumberExc, PhoneNumberFormatExc  {
		this.id = id;
		setAttributes(firstName, lastName, birthDate, phone);
	}
		
	public void setAttributes(final String firstName, final String lastName, final Date birthDate, final String phone) throws IncredibleBirthDateExc, EmptyPhoneNumberExc, PhoneNumberFormatExc {
		if( birthDate.before(beginDate) || birthDate.after(new Date()) ){
			throw create(IncredibleBirthDateExc.class, birthDate);
		}
		if(phone.length()<=0){
			throw new EmptyPhoneNumberExc();
		}
		if(!phone.matches(ALLOWED_PHONE_PATTERN)){
			throw create(PhoneNumberFormatExc.class, phone, ALLOWED_PHONE_CHARS);
		}
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.phone = phone;
	}
	
	public long getId(){
		return this.id;
	}
	
	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}
	
	public String getPhone() {
		return this.phone;
	}

	/**Date {0,date,long} is not credible as a client''s birth date*/
	public static class IncredibleBirthDateExc extends multex.Exc {}

	/**Phone number is empty*/
	public static class EmptyPhoneNumberExc extends multex.Exc {}
	
	/**Phone number {0} contains illegal characters. Allowed are only ''{1}''*/
	public static class PhoneNumberFormatExc extends multex.Exc {}
	
}
