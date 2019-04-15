package hu.selester.android.webstockandroid.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import hu.selester.android.webstockandroid.Adapters.XD_ItemsParametersListAdapter;
import hu.selester.android.webstockandroid.Divider.SimpleDividerItemDecoration;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.XD_ItemsParameters;
import hu.selester.android.webstockandroid.R;

public class XD_PlacenumberFragment extends Fragment {

    private View rootView;
    private String title,tranCode;
    private RecyclerView itemsListContainer;
    private int qEvidNum, qNeed, qCurrent, qWeight, qWidth, qHeight, qLength, qBarcode, qToPlace;
    private XD_ItemParametersFragment parentFragment;

    public void setParentFragment(XD_ItemParametersFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

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
        tranCode = SessionClass.getParam("tranCode");
        qBarcode = HelperClass.getArrayPosition("Barcode", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qEvidNum = HelperClass.getArrayPosition("EvidNum", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qNeed = HelperClass.getArrayPosition("Needed_Qty", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qCurrent = HelperClass.getArrayPosition("Current_Qty", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWeight  = HelperClass.getArrayPosition("Weight", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWidth   = HelperClass.getArrayPosition("Size_Width", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qHeight  = HelperClass.getArrayPosition("Size_Height", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qLength  = HelperClass.getArrayPosition("Size_Length", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qToPlace  = HelperClass.getArrayPosition("To_Place", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        update();


        /*
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
        });*/

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBarcodeReceiver, new IntentFilter("BROADCAST_BARCODE"));
        return rootView;
    }

    private BroadcastReceiver mBarcodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    public void update(){
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        List<XD_ItemsParameters> itemsList = new ArrayList<>();
        List<String[]> dataList = AllLinesData.findItemsFromMap(title, qToPlace);
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i)[qToPlace].equals(title)) {
                    itemsList.add(new XD_ItemsParameters(Long.parseLong(dataList.get(i)[0]),Integer.parseInt(dataList.get(i)[qNeed]), Integer.parseInt(dataList.get(i)[qCurrent]), Float.parseFloat(dataList.get(i)[qWeight]), Float.parseFloat(dataList.get(i)[qLength]), Float.parseFloat(dataList.get(i)[qWidth]), Float.parseFloat(dataList.get(i)[qHeight])));
                }
            }
        }
        XD_ItemsParametersListAdapter XD_listAdapter = new XD_ItemsParametersListAdapter(getContext(), itemsList,1);
        XD_listAdapter.setOnEventUpdate(new XD_ItemsParametersListAdapter.OnEventUpdate() {
            @Override
            public void onUpdatePanel() {
                parentFragment.updateTopItems();
            }
        });
        itemsListContainer = rootView.findViewById(R.id.vp_pn_container);
        itemsListContainer.setLayoutManager(lm);
        itemsListContainer.setAdapter(XD_listAdapter);
        SimpleDividerItemDecoration itemDecor = new SimpleDividerItemDecoration(getContext());
        itemsListContainer.addItemDecoration(itemDecor);
    }
}