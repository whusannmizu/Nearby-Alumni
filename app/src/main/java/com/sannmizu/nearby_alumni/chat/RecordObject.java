package com.sannmizu.nearby_alumni.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.sannmizu.nearby_alumni.Database.ChatRecord;

import java.util.Date;

public class RecordObject extends BaseObject implements Parcelable {
    public static final int TYPE_SEND = RecordAdapter.SendMsg;
    public static final int TYPE_RECEIVE = RecordAdapter.ReceiveMsg;
    private String content;
    private boolean isText;
    private Date time;

    public RecordObject(int type, String content, boolean isText, Date time) {
        super(type);
        this.content = content;
        this.isText = isText;
        this.time = time;
    }

    public RecordObject(ChatRecord chatRecord) {
        this(chatRecord.getSubject(), chatRecord.getContent(), chatRecord.isText(), chatRecord.getTime());
    }

    public RecordObject(Parcel parcel) {
        super(parcel.readInt());
        this.content = parcel.readString();
        this.isText = parcel.readInt() == 1;
        this.time = new Date(parcel.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(getType());
        parcel.writeString(content);
        parcel.writeInt(isText ? 1 : 0);
        parcel.writeLong(time.getTime());
    }

    public static final Parcelable.Creator<RecordObject> CREATOR
            = new Parcelable.Creator<RecordObject>() {
        @Override
        public RecordObject createFromParcel(Parcel parcel) {
            return new RecordObject(parcel);
        }

        @Override
        public RecordObject[] newArray(int i) {
            return new RecordObject[i];
        }
    };

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean text) {
        isText = text;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
