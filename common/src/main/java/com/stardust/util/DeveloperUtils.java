package com.stardust.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;

import com.stardust.BuildConfig;

/**
 * Created by Stardust on 2017/4/5.
 */

public class DeveloperUtils {

    private static final String PACKAGE_NAME = "com.stardust.scriptdroid";
    private static final String SIGNATURE = "3082037130820259a003020102020428425adc300d06092a864886f70d01010b05003068310b300906035504061302434e310f300d06035504080c06e5b9bfe4b89c310f300d06035504070c06e5b9bfe5b79e31123010060355040a0c09444b52e4b8aae4baba310c300a060355040b1303444b523115301306035504030c0ce6989fe5b098e5b9bbe5bdb13020170d3135313031303039353533365a180f32303730303731333039353533365a3068310b300906035504061302434e310f300d06035504080c06e5b9bfe4b89c310f300d06035504070c06e5b9bfe5b79e31123010060355040a0c09444b52e4b8aae4baba310c300a060355040b1303444b523115301306035504030c0ce6989fe5b098e5b9bbe5bdb130820122300d06092a864886f70d01010105000382010f003082010a0282010100d4377c8e7f711408c6136e6a25b1b3d0cc7dc2ebfd633a623bd24649b4d74b702a1792e298bd2e495b0b72226f06b9bd9078d1707fc2997e6732f5d253b3c318b1ca52a7fe82eb1045825ff2f85386701e9b4837ad77cdacb8e269d41fc52077dd6a2b6cfac70d3e5b5cae90880983d09bb2f09ec5e415c07a73ec0331aec0fe7ca9ea3de4ced22e3c0a94d4b0ae89bcf7bb73e3861897afad3099f5f9098c324d80e46cda00641fdb78cd451f3615553365b75835618b34cc27385f3a5c503185d2972d2efaba384e3bba1c9e353ea8ce898a34df76697253cdb0fb80866c6b2b04ab3b0ab3211789ace0d9b456854a549ce24c36ca30d73943a4dc111adbc90203010001a321301f301d0603551d0e04160414e8b844190be53497ca213ae3a81e2ce7883a0b21300d06092a864886f70d01010b050003820101007f9d7bfed50a22cdebec1134d82a23f33be739e5dd61fe4a84e1065230397ba02dde126aadcd0bb0dca22b7c3a396aae0f8ca1945c5b27d2ccea9b509a99a9d7f2fe7ba82118e851794134f0a13d53e2203e77f2e1a7e2ed0d732419d24902c30a75d556fa65aae6b137a3a2c48b732a30f906e446ec8a54e6f6f914a5fe8e33f0219635b3ad4b9d3b76e1c38b19030f12e0f110aac1a9335f542bbe49705f01f0082f8840be09d85514fdb92f08a4abe3811492323cec5b80cb3edc62e3ec44d426eff2adff817cfc37022e113d9cce442ecb95e343563eb081380631f9f0a6925dc1b717049160e4dfc4400d477dceaccfc8f2a7b73616619c9a802eb47ebd";

    public static void ensureRunningPackageNotSelf(@Nullable String runningPackage) {
        if (PACKAGE_NAME.equals(runningPackage)) {
            throw new SecurityException();
        }
    }

    public static boolean isSelfPackage(@Nullable String runningPackage) {
        return PACKAGE_NAME.equals(runningPackage);
    }

    @Nullable
    public static String getSignature(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            StringBuilder builder = new StringBuilder();
            for (Signature signature : signatures) {
                builder.append(signature.toCharsString());
            }
            return builder.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 此方法仅防止那些不会改源码直接用apk编辑器修改应用内字符串(QQ群号)等的恶意用户行为。
     * 为了开源社区的发展，请善用源码:-)
     */
    public static boolean checkSignature(Context context) {
        return SIGNATURE.equals(getSignature(context, context.getPackageName()));
    }

    public static boolean checkSignature(Context context, String packageName) {
        return SIGNATURE.equals(getSignature(context, packageName));
    }


}
