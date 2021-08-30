package mbaas.com.nifcloud.androidautologinapp;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.nifcloud.mbaas.core.DoneCallback;
import com.nifcloud.mbaas.core.LoginCallback;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBUser;

public class Utils {
    public static void deleteUserIfExist(Context context) throws NCMBException {
        final String uuid = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        NCMBUser.loginInBackground(uuid, uuid, new LoginCallback() {
            @Override
            public void done(NCMBUser ncmbUser, NCMBException e) {
                if (e == null) {
                    ncmbUser.deleteObjectInBackground(new DoneCallback() {
                        @Override
                        public void done(NCMBException e) {
                            if (e != null) {
                                Log.d("NCMB", e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }
}
