package no.bekk.boss.bpep.example;

public class Person {
	String firstname;
	String lastname;
	String address;
	String zipcode;
	String city;

	public Person(PersonBuilder personBuilder) {
		this.address = personBuilder.address;
	}
}
