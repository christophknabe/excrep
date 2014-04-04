package struts_ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.struts.action.ActionForm;

/** Holds the data of one Client as Strings. */
public class ClientForm extends ActionForm {


	private static final long serialVersionUID = -76182989770397270L;

	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	private String id;
	private String firstName;
	private String lastName;
	private String birthDate;
	private String phone;
	
	public String getId() {
		return this.id;
	}
	public void setId(final String id) {
		this.id = id;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}
	public String getBirthDate() {
		return this.birthDate;
	}
	public void setBirthDate(final String birthDate) throws ParseException {
		this.birthDate = birthDate;
	}
	public String getDateFormat(){
		return df.toPattern();
	}
	public String getPhone() {
		return this.phone;
	}
	public void setPhone(final String phone) {
		this.phone = phone;
	}
	
	//Conversion methods to/from internal types:
	
	/*package*/ long getInternalId(){
		return this.id.length()==0 ? 0 : Long.parseLong(this.id);
	}
	/*package*/ void setInternalId(final long id){
		this.id = Long.toString(id);
	}
	Date getInternalBirthDate() throws ParseException {
		return df.parse(this.birthDate);
	}
	void setInternalBirthDate(final Date internalBirthDate) {
		this.birthDate = df.format(internalBirthDate);
	}
	
	
}
