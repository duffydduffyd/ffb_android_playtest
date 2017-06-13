package com.foodfeedback.valueobjects;

  
public class Coach {
    
    private int itemType;
    private MemberInfo memberInfo;
    private InvitationItem mInvitation;
    
    public int getItemType() {
        return itemType;
    }
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
    public MemberInfo getMemberInfo() {
        return memberInfo;
    }
    public void setMemberInfo(MemberInfo mmemberInfo) {
        this.memberInfo = mmemberInfo;
    }
    public InvitationItem getmInvitation() {
        return mInvitation;
    }
    public void setmInvitation(InvitationItem mInvitation) {
        this.mInvitation = mInvitation;
    }
}
