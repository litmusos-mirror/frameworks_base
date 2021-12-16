/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.view.selectiontoolbar;

import android.annotation.NonNull;
import android.os.Parcelable;

import com.android.internal.util.DataClass;


/**
 * The class holds menu information for render service to render the selection toolbar.
 *
 * @hide
 */
@DataClass(genToString = true, genEqualsHashCode = true)
public final class ShowInfo implements Parcelable {
    /**
     * The token that is used to identify the selection toolbar. This is initially set to 0
     * until a selection toolbar has been created for the showToolbar request.
     */
    private final long mWidgetToken;

    // TODO: add members when the code really uses it




    // Code below generated by codegen v1.0.23.
    //
    // DO NOT MODIFY!
    // CHECKSTYLE:OFF Generated code
    //
    // To regenerate run:
    // $ codegen $ANDROID_BUILD_TOP/frameworks/base/core/java/android/view/selectiontoolbar/ShowInfo.java
    //
    // To exclude the generated code from IntelliJ auto-formatting enable (one-time):
    //   Settings > Editor > Code Style > Formatter Control
    //@formatter:off


    /**
     * Creates a new ShowInfo.
     *
     * @param widgetToken
     *   The token that is used to identify the selection toolbar.
     */
    @DataClass.Generated.Member
    public ShowInfo(
            long widgetToken) {
        this.mWidgetToken = widgetToken;

        // onConstructed(); // You can define this method to get a callback
    }

    /**
     * The token that is used to identify the selection toolbar.
     */
    @DataClass.Generated.Member
    public long getWidgetToken() {
        return mWidgetToken;
    }

    @Override
    @DataClass.Generated.Member
    public String toString() {
        // You can override field toString logic by defining methods like:
        // String fieldNameToString() { ... }

        return "ShowInfo { " +
                "widgetToken = " + mWidgetToken +
        " }";
    }

    @Override
    @DataClass.Generated.Member
    public boolean equals(@android.annotation.Nullable Object o) {
        // You can override field equality logic by defining either of the methods like:
        // boolean fieldNameEquals(ShowInfo other) { ... }
        // boolean fieldNameEquals(FieldType otherValue) { ... }

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @SuppressWarnings("unchecked")
        ShowInfo that = (ShowInfo) o;
        //noinspection PointlessBooleanExpression
        return true
                && mWidgetToken == that.mWidgetToken;
    }

    @Override
    @DataClass.Generated.Member
    public int hashCode() {
        // You can override field hashCode logic by defining methods like:
        // int fieldNameHashCode() { ... }

        int _hash = 1;
        _hash = 31 * _hash + Long.hashCode(mWidgetToken);
        return _hash;
    }

    @Override
    @DataClass.Generated.Member
    public void writeToParcel(@NonNull android.os.Parcel dest, int flags) {
        // You can override field parcelling by defining methods like:
        // void parcelFieldName(Parcel dest, int flags) { ... }

        dest.writeLong(mWidgetToken);
    }

    @Override
    @DataClass.Generated.Member
    public int describeContents() { return 0; }

    /** @hide */
    @SuppressWarnings({"unchecked", "RedundantCast"})
    @DataClass.Generated.Member
    /* package-private */ ShowInfo(@NonNull android.os.Parcel in) {
        // You can override field unparcelling by defining methods like:
        // static FieldType unparcelFieldName(Parcel in) { ... }

        long widgetToken = in.readLong();

        this.mWidgetToken = widgetToken;

        // onConstructed(); // You can define this method to get a callback
    }

    @DataClass.Generated.Member
    public static final @NonNull Parcelable.Creator<ShowInfo> CREATOR
            = new Parcelable.Creator<ShowInfo>() {
        @Override
        public ShowInfo[] newArray(int size) {
            return new ShowInfo[size];
        }

        @Override
        public ShowInfo createFromParcel(@NonNull android.os.Parcel in) {
            return new ShowInfo(in);
        }
    };

    @DataClass.Generated(
            time = 1639488262761L,
            codegenVersion = "1.0.23",
            sourceFile = "frameworks/base/core/java/android/view/selectiontoolbar/ShowInfo.java",
            inputSignatures = "private final  long mWidgetToken\nclass ShowInfo extends java.lang.Object implements [android.os.Parcelable]\n@com.android.internal.util.DataClass(genToString=true, genEqualsHashCode=true)")
    @Deprecated
    private void __metadata() {}


    //@formatter:on
    // End of generated code

}