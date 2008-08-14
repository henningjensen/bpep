package no.bekk.boss.bpep.example;

public class PersonBuilder {
	String firstname;
	String lastname;
	String address;
	String zipcode;
	String city;
	
	public Person build() {
		return new Person(this);
	}

	public PersonBuilder lastname(String address) {
		this.lastname = address;
		return this;
	}
}
