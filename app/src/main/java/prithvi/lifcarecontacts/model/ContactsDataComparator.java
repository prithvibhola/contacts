package prithvi.lifcarecontacts.model;

import java.util.Comparator;

/**
 * Created by Prithvi on 3/25/2017.
 */

public class ContactsDataComparator implements Comparator<ContactsData> {

    @Override
    public int compare(ContactsData o1, ContactsData o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
