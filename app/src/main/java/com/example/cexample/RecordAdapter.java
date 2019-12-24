package com.example.cexample;

/*com.example.vs00481543.phonecallrecorder*/

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;

/**
 * Created by VS00481543 on 03-11-2017.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS =12345 ;
    List<CallDetails> callDetails;
    Context context;
    SharedPreferences pref;
    String checkDate = "";
    static String  Fname;
    private View dialogView;
    private AlertDialog.Builder builder2;
    private View CardL;

    public RecordAdapter(List<CallDetails> callDetails, Context context) {
        this.callDetails = callDetails;
        this.context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView number, time, date,name;

        public MyViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date1);
            name = (TextView) itemView.findViewById(R.id.name1);
            number = (TextView) itemView.findViewById(R.id.num);
            time = (TextView) itemView.findViewById(R.id.time1);
        }

        public void bind(final String dates, final String number, final String times,RecordAdapter.MyViewHolder holder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence options[] = new CharSequence[]
                            {

                                    "Play Recording",
                                    "Rename"
                            };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle( "Choose  Option");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                Toast.makeText(context, "Clicked on " + number, Toast.LENGTH_SHORT).show();
                                String path = Environment.getExternalStorageDirectory() + "/My Records/" + dates + "/" + number + "_" + times + ".mp4"  ;
                                Log.d("path", "onClick: "+path);
//                    Uri uri = Uri.parse(path);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                File file = new File(path);
//                    intent.setDataAndType(Uri.fromFile(file), "audio/*");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setDataAndType(getUriForFile(context,"com.example.cexample",file), "audio/*");
                                context.startActivity(intent);
                                pref.edit().putBoolean("pauseStateVLC",true).apply();
                            } else if (i == 1) {
                                Toast.makeText(context, "Chaneg the Name", Toast.LENGTH_SHORT).show();
                                //  showDialog(Fname);

                            }
                        }
                    });
                    builder.show();
/*
                                Toast.makeText(context, "Clicked on " + number, Toast.LENGTH_SHORT).show();
                    String path = Environment.getExternalStorageDirectory() + "/My Records/" + dates + "/" + number + "_" + times + ".mp4"  ;
                    Log.d("path", "onClick: "+path);
//                    Uri uri = Uri.parse(path);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(path);
//                    intent.setDataAndType(Uri.fromFile(file), "audio/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(getUriForFile(context,"com.example.cexample",file), "audio/*");
                    context.startActivity(intent);

                    pref.edit().putBoolean("pauseStateVLC",true).apply();*/

                    /*FileInputStream fis=null;
                    MediaPlayer mp=new MediaPlayer();
                    try {
                        fis=new FileInputStream(path);
                        mp.setDataSource(fis.getFD());
                        fis.close();
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                        }
                    });*/
                }
            });
        }
    }
    public boolean checkPermissionForReadContacts() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    public void showDialog(final String msg, final String n, final String Fname , final int id) {
        final Dialog d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
/*        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.BLACK));*/
        d.setContentView(R.layout.window_agreement);
        d.setCancelable(false);
        final String[] name = new String[1];
        final EditText tv = (EditText) d.findViewById(R.id.rename);
        tv.setText(msg);
        Button btn = (Button) d.findViewById(R.id.idbtnagreement);
        btn.setText("OK");
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                ArrayList<ContentProviderOperation> ops =
                        new ArrayList<ContentProviderOperation>();

                name[0] = tv.getText().toString();

                if(Fname.equals("Unknown")) {
                    addContacts(name[0], n);
                }
                else
                {
                    upDateContacts(context,name[0],n,id);
                }


                   /* ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                            .build()
                    );

                    //------------------------------------------------------ Names
                    if (Fname != null) {
                        ops.add(ContentProviderOperation.newInsert(
                                ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                .withValue(
                                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                        name[0]).build()
                        );
                    }

                    //------------------------------------------------------ Mobile Number
                    if (n != null) {
                        ops.add(ContentProviderOperation.
                                newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, n)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                .build()
                        );
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                try
                {


                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                Manifest.permission.READ_CONTACTS)) {
                            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

                            *//*  Log.i("Logs"+)*//*
                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    //  Toast.makeText(myContext, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }*/
                d.dismiss();
            }
        });
        d.show();
    }

    public static boolean upDateContacts(Context context,String name,String newPhoneNumber,int id){
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        // Name
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " +
                ContactsContract.Data.MIMETYPE + "=?", new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, name);
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);

        ops.add(builder.build());

        // Number
        builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " +
                ContactsContract.Data.MIMETYPE + "=?"+ " AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=?", new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)});
        builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber);
        ops.add(builder.build());

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private void addContacts(String name, String phone) {
        if (checkPermissionForReadContacts() == true) {
            ContentValues values = new ContentValues();
            values.put(Contacts.People.NUMBER, phone);
            values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
            values.put(Contacts.People.LABEL, name);
            values.put(Contacts.People.NAME, name);
            Uri dataUri = context.getContentResolver().insert(Contacts.People.CONTENT_URI, values);
            Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
            values.clear();
            values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
            values.put(Contacts.People.NUMBER, phone);
            updateUri = context.getContentResolver().insert(updateUri, values);
            Log.d("CONTACT", "" + updateUri);
        }
    }

    @Override
    public RecordAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder viewHolder = null;
        LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View v1 = layoutInflator.inflate(R.layout.record_list, parent, false);
                viewHolder = new MyViewHolder(v1);
                break;
            /*case 1:
                View v2 = layoutInflator.inflate(R.layout.record_noname_list, parent, false);
                viewHolder = new MyViewHolder(v2);
                break;*/
            case 2:
                View v3 = layoutInflator.inflate(R.layout.date_layout, parent, false);
                viewHolder = new MyViewHolder(v3);
                break;
            /*case 3:
                View v4 = layoutInflator.inflate(R.layout.date_noname_layout, parent, false);
                viewHolder = new MyViewHolder(v4);
                break;*/
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecordAdapter.MyViewHolder holder, int position) {
        final CallDetails cd1 = callDetails.get(position);
        String n=cd1.getNum();
        String name=new CommonMethods().getContactName(n,context);
        String name2="Unknown";
        cd1.getSerial();

     /*   if(name!=null && !name.equals("")) {
            Fname=name;
        }
        else {
            Fname=name2;
        }*/
        Log.d("Names", "onBindViewHolder: "+name);
        switch (getItemViewType(position)) {
            case 0:
                if(name!=null && !name.equals("")) {
                    Fname=name;
                    holder.name.setText(Fname);
                    holder.name.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    Fname=name2;
                    holder.name.setText(Fname);
                    holder.name.setTextColor(context.getResources().getColor(R.color.red));
                }
                holder.number.setText(callDetails.get(position).getNum());
                holder.time.setText(callDetails.get(position).getTime1());
                break;
            /*case 1:
                holder.number.setText(callDetails.get(position).getNum());
                holder.time.setText(callDetails.get(position).getTime1());
                break;*/
            case 2:
                holder.date.setText(callDetails.get(position).getDate1());
                if(name!=null && !name.equals("")) {
                    Fname=name;
                    holder.name.setText(Fname);
                    holder.name.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    Fname=name2;
                    holder.name.setText(Fname);
                    holder.name.setTextColor(context.getResources().getColor(R.color.red));
                }
                holder.number.setText(callDetails.get(position).getNum());
                holder.time.setText(callDetails.get(position).getTime1());
                break;
            /*case 3:
                holder.date.setText(callDetails.get(position).getDate1());
                holder.number.setText(callDetails.get(position).getNum());
                holder.time.setText(callDetails.get(position).getTime1());
                break;*/
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]
                        {

                                "Play Recording",
                                "Rename"
                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle( "Choose  Option");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Toast.makeText(context, "Clicked on " + cd1.getNum(), Toast.LENGTH_SHORT).show();
                            String path = Environment.getExternalStorageDirectory() + "/My Records/" + cd1.getDate1() + "/" + cd1.getNum() + "_" + cd1.getTime1() + ".mp4"  ;
                            Log.d("path", "onClick: "+path);
//                    Uri uri = Uri.parse(path);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            File file = new File(path);
//                    intent.setDataAndType(Uri.fromFile(file), "audio/*");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(getUriForFile(context,"com.example.cexample",file), "audio/*");
                            context.startActivity(intent);
                            pref.edit().putBoolean("pauseStateVLC",true).apply();
                        } else if (i == 1) {

                            int id=cd1.getSerial();
                            String n=cd1.getNum();
                            String name=new CommonMethods().getContactName(n,context);
                            String name2="Unknown";
                            if(name!=null && !name.equals("")) {
                                Fname=name;
                            }
                            else {
                                Fname=name2;
                            }
                            Toast.makeText(context, "Chaneg the Name", Toast.LENGTH_SHORT).show();

                            showDialog(Fname,n,Fname,id);
                        }
                    }
                });
                builder.show();
/*\
}
 */
            }
        });
        //  holder.bind(cd1.getDate1(), cd1.getNum(),cd1.getTime1(),holder);

    }

    @Override
    public int getItemCount() {
        return callDetails.size();
    }

    public int getItemViewType(int position) {
        CallDetails cd = callDetails.get(position);
        String dt = cd.getDate1();
        Log.d("Adapter", "getItemViewType: " + dt);
        Log.d("Adapter", "getItemViewType: " + pref.getString("date", ""));
        // String checkDate=pref.getString("date","");

        try {
            if (position!=0 && cd.getDate1().equalsIgnoreCase(callDetails.get(position - 1).getDate1())) {
                checkDate = dt;
                //pref.edit().putString("date",dt).apply();
                Log.d("Adapter", "getItemViewType: in if condition" + pref.getString("date", ""));
                return 0;
                /*if(name1!=null && !name1.equals(""))
                    return 0;
                else
                    return 1;*/
            } else {
                checkDate = dt;
                //pref.edit().putString("date",dt).apply();
                Log.d("Adapter", "getItemViewType: in else condition" + pref.getString("date", ""));
               /* if(name1!=null && !name1.equals(""))
                    return 2;
                else
                    return 3;*/
                return 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }
}
