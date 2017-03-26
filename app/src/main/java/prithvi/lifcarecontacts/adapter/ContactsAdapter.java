package prithvi.lifcarecontacts.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import prithvi.lifcarecontacts.R;
import prithvi.lifcarecontacts.model.ContactsData;

/**
 * Created by Prithvi on 3/25/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<ContactsData> contactsList = new ArrayList<>();
    private LayoutInflater layoutInflater;

    private ContactsAdapterOnClickHandler clickHandler;

    public ContactsAdapter(Context context, ContactsAdapterOnClickHandler vh){
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.clickHandler = vh;
    }

    public void setAppList(ArrayList<ContactsData> contactsList){
        this.contactsList = contactsList;
        notifyDataSetChanged();
//        notifyItemRangeChanged(0, contactsList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.holder_contacts, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ContactsData currentContact = contactsList.get(position);

        holder.textName.setText(currentContact.getName());
        holder.textNumber.setText(currentContact.getMobileNumber());
        if (currentContact.getImageUrl() != null) {
            holder.imageContactText.setVisibility(View.GONE);
            holder.imageContact.setVisibility(View.VISIBLE);
            holder.imageContact.setImageURI(Uri.parse(currentContact.getImageUrl()));
        }else{
            holder.imageContactText.setVisibility(View.VISIBLE);
            holder.imageContact.setVisibility(View.GONE);
            TextDrawable drawable = TextDrawable.builder().buildRound(currentContact.getName().charAt(0) + "", Color.RED);
            holder.imageContactText.setImageDrawable(drawable);
        }

    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.ivContactImage)
        CircularImageView imageContact;
        @BindView(R.id.ivContactImageText)
        ImageView imageContactText;
        @BindView(R.id.tvContactName)
        TextView textName;
        @BindView(R.id.tvContactNumber)
        TextView textNumber;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(getAdapterPosition() >= 0)
                clickHandler.onClick(contactsList.get(getAdapterPosition()), this);
        }
    }

    public interface ContactsAdapterOnClickHandler{
        void onClick(ContactsData contactsData, RecyclerView.ViewHolder vh);
    }
}