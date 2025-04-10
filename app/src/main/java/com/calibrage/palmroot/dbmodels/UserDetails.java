package com.calibrage.palmroot.dbmodels;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetails  implements Parcelable {
    public String UserId;
    public String UserName;
    public String Password;
    public int RoleId;
    public int ManagerId;
    public int TabletId;
    public String userVillageId;
    public String Id;
    public String FirstName;
    public String roleCode;

    /* public String getLastName() {
         return LastName;
     }

     public void setLastName(String lastName) {
         LastName = lastName;
     }
 */
    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public static Creator<UserDetails> getCREATOR() {
        return CREATOR;
    }

    public String userCode;

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String tabName;


    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getFirstName() {
        return FirstName;
    }


    public void setId(String Id) {
        this.Id = Id;
    }

    public String getId() {
        return Id;
    }

    public String getUserVillageId() {
        return userVillageId;
    }

    public void setUserVillageId(String userVillageId) {
        this.userVillageId = userVillageId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getRoleId() {
        return RoleId;
    }

    public void setRoleId(int roleId) {
        RoleId = roleId;
    }

    public int getManagerId() {
        return ManagerId;
    }

    public void setManagerId(int managerId) {
        ManagerId = managerId;
    }

    public int getTabletId() {
        return TabletId;
    }

    public void setTabletId(int tabletId) {
        TabletId = tabletId;
    }

    public UserDetails() {

    }

    protected UserDetails(Parcel in) {
        UserId = in.readString();
        UserName = in.readString();
        Password = in.readString();
        RoleId = in.readInt();
        ManagerId = in.readInt();
        TabletId = in.readInt();
        Id = in.readString();
        tabName = in.readString();
        userCode = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UserId);
        dest.writeString(UserName);
        dest.writeString(Password);
        dest.writeInt(RoleId);
        dest.writeInt(ManagerId);
        dest.writeInt(TabletId);
        dest.writeString(Id);
        dest.writeString(tabName);
        dest.writeString(userCode);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserDetails> CREATOR = new Parcelable.Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
}