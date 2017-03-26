package prithvi.lifcarecontacts.activity;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import prithvi.lifcarecontacts.R;
import prithvi.lifcarecontacts.adapter.ContactsAdapter;
import prithvi.lifcarecontacts.model.ContactsData;
import prithvi.lifcarecontacts.utils.Permissions;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.rvContacts)
    RecyclerView mRecyclerView;

    ArrayList<ContactsData> contactsList;
    ContactsAdapter mContactsAdapter;

    private final int PERMISSIONS_REQUEST_READ_PHONE_CONTACTS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        contactsList = new ArrayList<>();
        mContactsAdapter = new ContactsAdapter(this, new ContactsAdapter.ContactsAdapterOnClickHandler(){

            @Override
            public void onClick(ContactsData contactsData, RecyclerView.ViewHolder vh) {
                bottomSheetDialogLayout(contactsData.getName(), contactsData.getMobileNumber(), contactsData.getMimeType(), contactsData.getRawContactID());
            }
        });

        if(isPermissionAllowed()){
            mContactsAdapter.setAppList(getContacts(getApplicationContext().getContentResolver()));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerView.setAdapter(mContactsAdapter);
        }else{
            requestPermission();
        }
    }

    private boolean isPermissionAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_PHONE_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_PHONE_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mContactsAdapter.setAppList(getContacts(getApplicationContext().getContentResolver()));
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    mRecyclerView.setAdapter(mContactsAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private ArrayList<ContactsData> getContacts(ContentResolver contentResolver){

        HashSet<String> uniquePhone = new HashSet<>();

        Cursor contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (contacts.moveToNext()){
            ContactsData contactsData = new ContactsData();
            if(uniquePhone.add(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))){
                contactsData.setName(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contactsData.setMobileNumber(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contactsData.setImageUrl(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
                contactsData.setMimeType(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.MIMETYPE)));
                contactsData.setRawContactID(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID)));
                contactsList.add(contactsData);
            }
        }
        contacts.close();
        Collections.sort(contactsList, new Comparator<ContactsData>() {
            @Override
            public int compare(ContactsData lhs, ContactsData rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        return contactsList;
    }

    public void bottomSheetDialogLayout(final String name, final String mobileNumber, final String mimeType, final String rawContactID) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.contact_edit_sheet);

        final TextView textName = (TextView) dialog.findViewById(R.id.tvName);
        final EditText editNumber = (EditText) dialog.findViewById(R.id.etNumber);
        Button buttonDelete = (Button) dialog.findViewById(R.id.bDelete);
        Button buttonSave = (Button) dialog.findViewById(R.id.bSave);

        textName.setText(name);
        editNumber.setText(mobileNumber);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = updateContactList(editNumber.getText().toString(), mimeType, rawContactID);
                if(status) {
                    Toast.makeText(getApplicationContext(), "Edited!!", Toast.LENGTH_SHORT).show();
                    contactsList.clear();
                    mContactsAdapter.setAppList(getContacts(getApplicationContext().getContentResolver()));
                }else {
                    Toast.makeText(getApplicationContext(), "Some error occured!!", Toast.LENGTH_SHORT).show();
                }
                dialog.hide();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = deleteContactList(name);
                if(status) {
                    Toast.makeText(getApplicationContext(), "Deleted!!", Toast.LENGTH_SHORT).show();
                    contactsList.clear();
                    mContactsAdapter.setAppList(getContacts(getApplicationContext().getContentResolver()));
                }else {
                    Toast.makeText(getApplicationContext(), "Some error occured!!", Toast.LENGTH_SHORT).show();
                }
                dialog.hide();
            }
        });
        dialog.show();
    }

    public boolean updateContactList(String newPhoneNumber, String mimeType, String rawContactID) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        String where = ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = ? AND " + ContactsContract.CommonDataKinds.Phone.MIMETYPE + " = ? ";
        String[] params = new String[]{rawContactID, mimeType};

        //Edit the number
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, params)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
                .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteContactList(String name) {

        ContentResolver cr = getContentResolver();
        String where = ContactsContract.Data.DISPLAY_NAME + " = ? ";
        String[] params = new String[]{name};

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(where, params)
                .build());
        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
