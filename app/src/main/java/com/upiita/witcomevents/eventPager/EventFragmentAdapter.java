package com.upiita.witcomevents.eventPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.upiita.witcomevents.R;
import com.upiita.witcomevents.activities.ActivitiesActivity;
import com.upiita.witcomevents.database.EventDatabase;
import com.upiita.witcomevents.people.PeopleListActivity;
import com.upiita.witcomevents.schedule.ScheduleActivity;

import static com.upiita.witcomevents.SplashScreen.PAGES_CONTENT;
import static com.upiita.witcomevents.SplashScreen.PAGES_IMAGES;
import static com.upiita.witcomevents.eventPager.EventPagerActivity.mPager;

/**
 * Created by oscar on 6/05/17.
 */

public class EventFragmentAdapter extends FragmentPagerAdapter {

    public EventFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("Page", "Pos: " + position);
        return PlaceholderFragment.newInstance(position+1);
    }

    @Override
    public int getCount() {

        return PAGES_CONTENT.size();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.pager_section_label);
            ImageView imgV = (ImageView)rootView.findViewById(R.id.pager_image_section);

            textView.setText(PAGES_CONTENT.get(getArguments().getInt(ARG_SECTION_NUMBER)-1));
            imgV.setImageDrawable(getResources().getDrawable(PAGES_IMAGES.get(PAGES_CONTENT.get(getArguments().getInt(ARG_SECTION_NUMBER)-1))));



            imgV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), rootView.findViewById(R.id.pager_image_section), getString(R.string.transition_pager));

                    String aux = PAGES_CONTENT.get(mPager.getCurrentItem());

                    if(aux.equals(getString(R.string.streamings))) {
                        Toast.makeText(getContext(), getString(R.string.streamings), Toast.LENGTH_SHORT).show();
                    } else if (aux.equals(getString(R.string.schedule))) {
                        Intent intent = new Intent(getContext(), ScheduleActivity.class);
                        startActivity(intent);
                    } else if (aux.equals(getString(R.string.activities))) {
                        Intent intent = new Intent(getContext(), ActivitiesActivity.class);
                        startActivity(intent);
                    } else if (aux.equals(getString(R.string.people))) {
                        //Toast.makeText(getContext(), getString(R.string.people), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), PeopleListActivity.class);
                        startActivity(intent);
                    } else if (aux.equals(getString(R.string.how_to_arrive))) {
                        Toast.makeText(getContext(), getString(R.string.how_to_arrive), Toast.LENGTH_SHORT).show();

                        String latAux = "";
                        String longAux = "";

                        SQLiteDatabase db = new EventDatabase(getContext()).getReadableDatabase();
                        Cursor fila = db.rawQuery("SELECT place FROM event", null);

                        if (fila.moveToFirst()) {

                            do {
                                Cursor fila2 = db.rawQuery("SELECT * FROM place where id=" + fila.getString(0), null);
                                if(fila2.moveToFirst()) {
                                    latAux = fila2.getString(4);
                                    longAux = fila2.getString(3);
                                }
                                fila2.close();
                            } while (fila.moveToNext());
                        }

                        fila.close();
                        db.close();

                        final String longitude = longAux;
                        final String latitude = latAux;

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                        alertDialogBuilder
                                .setCancelable(false)
                                .setTitle(getString(R.string.navigation))
                                .setMessage(getString(R.string.data_usage))
                                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LayoutInflater inflater = (LayoutInflater)rootView.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                                        final View layout = inflater.inflate(R.layout.navigationmap, null);
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                                        alertDialogBuilder
                                                .setTitle(getString(R.string.navigation))
                                                .setMessage(getString(R.string.how_get))
                                                .setCancelable(false)
                                                .setView(layout)
                                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        RadioButton rbc = (RadioButton)layout.findViewById(R.id.car);
                                                        RadioButton rbb = (RadioButton)layout.findViewById(R.id.bike);
                                                        RadioButton rbw = (RadioButton)layout.findViewById(R.id.walk);

                                                        if(rbc.isChecked()) {
                                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);
                                                            dialog.cancel();
                                                        }
                                                        else if(rbb.isChecked()) {
                                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=b");
                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);
                                                            dialog.cancel();
                                                        }
                                                        else if(rbw.isChecked()) {
                                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w");
                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);
                                                            dialog.cancel();
                                                        }

                                                    }
                                                })
                                                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else if (aux.equals(getString(R.string.sketch))) {
                        Toast.makeText(getContext(), getString(R.string.sketch), Toast.LENGTH_SHORT).show();
                    } else if (aux.equals(getString(R.string.places))) {
                        Toast.makeText(getContext(), getString(R.string.places), Toast.LENGTH_SHORT).show();
                    } else if (aux.equals(getString(R.string.sponsors_and_about))) {
                        Toast.makeText(getContext(), getString(R.string.sponsors_and_about), Toast.LENGTH_SHORT).show();
                    }

                    /*if(mPager.getCurrentItem() == 0) {

                        startActivity(new Intent(getActivity(), EventListActivity.class));
                    }
                    else if(mPager.getCurrentItem() == 1) {

                        Intent intent = new Intent(rootView.getContext(), EventListActivity.class);
                        intent.putExtra("page", mPager.getCurrentItem()+1);
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

                    }

                    else if(mPager.getCurrentItem() == 2) {

                        Intent intent = new Intent(rootView.getContext(), EventListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("page", mPager.getCurrentItem()+1);
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

                        //ActivityCompat.startActivity(getActivity(), new Intent(rootView.getContext(), WitcomProgramActivity.class), options.toBundle());
                    }

                    else if(mPager.getCurrentItem() == 3) {
                        Intent intent = new Intent(rootView.getContext(), EventListActivity.class);
                        intent.putExtra("page", mPager.getCurrentItem()+1);
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                    }

                    else if(mPager.getCurrentItem() == 4) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                        alertDialogBuilder
                                .setCancelable(false)
                                .setTitle(getString(R.string.navigation))
                                .setMessage(getString(R.string.data_usage))
                                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LayoutInflater inflater = (LayoutInflater)rootView.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                                        final View layout = inflater.inflate(R.layout.navigationmap, null);
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                                        alertDialogBuilder
                                                .setTitle(getString(R.string.navigation))
                                                .setMessage(getString(R.string.how_get))
                                                .setCancelable(false)
                                                .setView(layout)
                                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        RadioButton rbc = (RadioButton)layout.findViewById(R.id.car);
                                                        RadioButton rbb = (RadioButton)layout.findViewById(R.id.bike);
                                                        RadioButton rbw = (RadioButton)layout.findViewById(R.id.walk);

                                                        if(rbc.isChecked()) {
                                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + "21.0152018" + "," + "-101.5028277");
                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);
                                                            dialog.cancel();
                                                        }
                                                        else if(rbb.isChecked()) {
                                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + "21.0152018" + "," + "-101.5028277" + "&mode=b");
                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);
                                                            dialog.cancel();
                                                        }
                                                        else if(rbw.isChecked()) {
                                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + "21.0152018" + "," + "-101.5028277" + "&mode=w");
                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);
                                                            dialog.cancel();
                                                        }

                                                    }
                                                })
                                                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }

                    else if(mPager.getCurrentItem() == 5) {
                        Intent intent = new Intent(rootView.getContext(),EventListActivity.class);
                        intent.putExtra("page", mPager.getCurrentItem()+1);
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                    }
                    else if(mPager.getCurrentItem() == 6) {
                        Intent intent = new Intent(rootView.getContext(),EventListActivity.class);
                        intent.putExtra("page", mPager.getCurrentItem()+1);
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                    }
                    else if(mPager.getCurrentItem() == 7) {
                        new AlertDialog.Builder(getContext())
                                .setView(R.layout.about)
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }*/
                }
            });

            return rootView;

        }
    }
}
