package no.bekk.boss.bpep.example;

public class PersonCreator {
	public static void main(String[] args) {
		Person person = new PersonBuilder().lastname("jensen").build();
		person.toString();
	}
}
