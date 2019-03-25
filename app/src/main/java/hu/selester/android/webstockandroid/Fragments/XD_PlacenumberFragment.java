package hu.selester.android.webstockandroid.Fragments;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import hu.selester.android.webstockandroid.R;

public class XD_PlacenumberFragment extends Fragment {

    private View rootView;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // newInstance constructor for creating fragment with arguments
    public static XD_PlacenumberFragment newInstance( String title) {
        XD_PlacenumberFragment fragment = new XD_PlacenumberFragment();
        Bundle args = new Bundle();
        args.putString("placenum", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.title =  getArguments().getString("placenum");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.vp_placenumber, container, false);

        rootView.findViewById(R.id.vp_pn_container).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {
                int action = event.getAction();
                // Handles each of the expected events
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Determines if this View can accept the dragged data
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            // if you want to apply color when drag started to your view you can uncomment below lines
                            // to give any color tint to the View to indicate that it can accept
                            // data.

                            //  view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);//set background color to your view

                            // Invalidate the view to force a redraw in the new tint
                            //  view.invalidate();

                            // returns true to indicate that the View can accept the dragged data.
                            return true;

                        }

                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        return false;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        // Applies a YELLOW or any color tint to the View, when the dragged view entered into drag acceptable view
                        // Return true; the return value is ignored.

                        view.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);


                        // Invalidate the view to force a redraw in the new tint
                        view.invalidate();

                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        // Ignore the event
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        // Re-sets the color tint to blue, if you had set the BLUE color or any color in ACTION_DRAG_STARTED. Returns true; the return value is ignored.

                        //  view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

                        //If u had not provided any color in ACTION_DRAG_STARTED then clear color filter.
                        view.getBackground().clearColorFilter();
                        // Invalidate the view to force a redraw in the new tint
                        view.invalidate();

                        return true;
                    case DragEvent.ACTION_DROP:
                        // Gets the item containing the dragged data
                        ClipData.Item item = event.getClipData().getItemAt(0);

                        // Gets the text data from the item.
                        String dragData = item.getText().toString();

                        // Displays a message containing the dragged data.
                        Toast.makeText(getContext(), "Dragged data is " + dragData, Toast.LENGTH_SHORT).show();

                        // Turns off any color tints
                        view.getBackground().clearColorFilter();

                        // Invalidates the view to force a redraw
                        view.invalidate();

                        View v = (View) event.getLocalState();
                        ViewGroup owner = (ViewGroup) v.getParent();
                        owner.removeView(v);//remove the dragged view
                        LinearLayout container = (LinearLayout) view;//caste the view into LinearLayout as our drag acceptable layout is LinearLayout
                        container.addView(v);//Add the dragged view
                        v.setVisibility(View.VISIBLE);//finally set Visibility to VISIBLE

                        // Returns true. DragEvent.getResult() will return true.
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // Turns off any color tinting
                        view.getBackground().clearColorFilter();

                        // Invalidates the view to force a redraw
                        view.invalidate();

                        // Does a getResult(), and displays what happened.
                        if (event.getResult())
                            Toast.makeText(getContext(), "The drop was handled.", Toast.LENGTH_SHORT).show();

                        else
                            Toast.makeText(getContext(), "The drop didn't work.", Toast.LENGTH_SHORT).show();


                        // returns true; the value is ignored.
                        return true;

                    // An unknown action type was received.
                    default:
                        Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                        break;
                }
                return false;
            }
        });
        return rootView;
    }
}
