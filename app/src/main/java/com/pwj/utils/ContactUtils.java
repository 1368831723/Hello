package com.pwj.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.pwj.bean.MyContacts;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019/6/21.
 */

public class ContactUtils {
    public static ArrayList<MyContacts> getAllContacts(Context context) {
        ArrayList<MyContacts> contacts = new ArrayList<MyContacts>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //新建一个联系人实例
            MyContacts myContacts = new MyContacts();

            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人姓名
            String contact = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            myContacts.contact = contact;
            Log.e("getAllContacts: ", "contact: " + contact);

            //获取联系人与他的关系
            Cursor cursor_relation = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    null, ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                    new String[]{contactId, ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE}, null);
            while (cursor_relation.moveToNext()) {
                String relation = cursor_relation.getString(cursor_relation.getColumnIndex(ContactsContract.CommonDataKinds.Relation.NAME));
                relation = relation.replace("-", "");
                relation = relation.replace(" ", "");
                myContacts.relation = relation;
            }
            //获取联系人电话号码
            Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while (phoneCursor.moveToNext()) {
                String phone_number= phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone_number = phone_number.replace("-", "");
                phone_number = phone_number.replace(" ", "");
                myContacts.phone_number = phone_number;
            }
            //获取联系人地址
            Cursor cursor_address = context.getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + "=" + contactId, null, null);
            while (cursor_address.moveToNext()) {
                String address = cursor_address.getString(cursor_address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA));
                address = address.replace("-", "");
                address = address.replace(" ", "");
                myContacts.address = address;
                Log.e("getAllContacts: ", "address: " + address);
            }

            //获取联系人公司名字
            Cursor cursor_company = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    null, ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                    new String[]{contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}, null);
            while (cursor_company.moveToNext()) {
                String company = cursor_company.getString(cursor_company.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                String company_position = cursor_company.getString(cursor_company.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                myContacts.company = company;
                myContacts.company_position = company_position;
                Log.e("getAllContacts: ", "company: " + company + " position:" + company_position);
            }
            //获取联系人电话号码
            Cursor cursor_email = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId, null, null);
            while (cursor_email.moveToNext()) {
                String email = cursor_email.getString(cursor_email.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                email = email.replace("-", "");
                email = email.replace(" ", "");
                myContacts.email = email;
                Log.e("getAllContacts: ", "email: " + email);
            }

            //获取联系人备注信息
            Cursor noteCursor = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,null,
                    ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",new String[]{contactId, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE}, null);
            if (noteCursor.moveToFirst()) {
                do {
                    String note = noteCursor.getString(noteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    if (note!=null&&!"".equals(note)){
                        if (note.contains("抛丸")){
                            myContacts.pwj = "是";
                        }else{
                            myContacts.pwj = "否";
                        }
                    }else {
                        myContacts.pwj = "否";
                    }
                    myContacts.remarks = note;
                    Log.i("note:", note);
                } while (noteCursor.moveToNext());
            }
            contacts.add(myContacts);
            //记得要把cursor给close掉
            cursor_relation.close();
            phoneCursor.close();
            cursor_address.close();
            cursor_company.close();
            cursor_email.close();
            noteCursor.close();
        }
        cursor.close();
        return contacts;
    }
}
