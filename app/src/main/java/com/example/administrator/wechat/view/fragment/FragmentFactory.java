package com.example.administrator.wechat.view.fragment;

/**
 * Created by Administrator on 2017/8/10.
 */

public class FragmentFactory {
    private static ConversationFragment sConversationFragment;
    private static ContactFragment sContactFragment;
    private static DongtaiFragment sDongtaiFragment;

    public static BaseFragment getFragment(int position) {
        switch (position) {
            case 0:
                if (sConversationFragment == null) {
                    sConversationFragment = new ConversationFragment();
                }
                return sConversationFragment;
            case 1:
                if (sContactFragment == null) {
                    sContactFragment = new ContactFragment();
                }
                return sContactFragment;

            case 2:
                if (sDongtaiFragment == null) {
                    sDongtaiFragment = new DongtaiFragment();
                }
                return sDongtaiFragment;
        }
        return null;
    }
}
