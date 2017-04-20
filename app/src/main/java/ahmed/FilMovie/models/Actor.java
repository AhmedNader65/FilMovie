package ahmed.FilMovie.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmed on 18/04/17.
 */

public class Actor implements Parcelable {
    private int id;
    private String name;
    private String characterName;
    private String picUrl;


    protected Actor(Parcel in) {
        id = in.readInt();
        name = in.readString();
        characterName = in.readString();
        picUrl = in.readString();
    }
    public Actor(int id, String name, String characterName, String picUrl){
        this.id = id;
        this.name = name;
        this.characterName = characterName;
        this.picUrl = picUrl;
    }
    public static final Creator<Actor> CREATOR = new Creator<Actor>() {
        @Override
        public Actor createFromParcel(Parcel in) {
            return new Actor(in);
        }

        @Override
        public Actor[] newArray(int size) {
            return new Actor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(characterName);
        parcel.writeString(picUrl);
    }
}
