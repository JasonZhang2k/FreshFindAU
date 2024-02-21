package comp5216.sydney.edu.au.freshfindau.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.page.EncyclopediaActivity;
import comp5216.sydney.edu.au.freshfindau.page.HomeActivity;
import comp5216.sydney.edu.au.freshfindau.page.MarketActivity;
import comp5216.sydney.edu.au.freshfindau.page.ProfileActivity;

public class NavigationUtils {
    public static void setupBottomNavigation(Activity activity, int activeButtonId) {
        Button homeButton = activity.findViewById(R.id.bottom_navigation_btn1);
        Button encyclopediaButton = activity.findViewById(R.id.bottom_navigation_btn2);
        Button marketButton = activity.findViewById(R.id.bottom_navigation_btn3);
        Button profileButton = activity.findViewById(R.id.bottom_navigation_btn4);

        // Set the color of the active button's icon
        updateBottomNavigationIconColor(activity.findViewById(activeButtonId), Color.BLACK);

        // Set click listeners
        homeButton.setOnClickListener(v -> activity.startActivity(new Intent(activity, HomeActivity.class)));
        encyclopediaButton.setOnClickListener(v -> activity.startActivity(new Intent(activity, EncyclopediaActivity.class)));
        marketButton.setOnClickListener(v -> activity.startActivity(new Intent(activity, MarketActivity.class)));
        profileButton.setOnClickListener(v -> activity.startActivity(new Intent(activity, ProfileActivity.class)));
    }

    public static void updateBottomNavigationIconColor(Button button, int color) {
        Drawable drawable = button.getCompoundDrawables()[1];
        if (drawable != null) {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
}
