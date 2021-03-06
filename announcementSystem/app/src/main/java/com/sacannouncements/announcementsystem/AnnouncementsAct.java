package com.sacannouncements.announcementsystem;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sacannouncements.announcementsystem.datamodel.AnnouncementCategoryDataModal;
import com.sacannouncements.announcementsystem.datamodel.AnnouncementNewsFeedDataModal;
import com.sacannouncements.announcementsystem.views.AnnouncementListRecyclerViewAdapter;
import com.sacannouncements.announcementsystem.views.NewsFeedListRecyclerViewAdapter;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class AnnouncementsAct extends AppCompatActivity {
    FirebaseFirestore db;

    ArrayList<AnnouncementCategoryDataModal> announcementCategoryDataModalArrayList = new ArrayList<>();
    SlidingRootNav slidingRootNav;
    AnnouncementListRecyclerViewAdapter announcementListRecyclerViewAdapter;
    RecyclerView announcementCatList,newsFeedList;
    ArrayList<AnnouncementNewsFeedDataModal> announcementNewsFeedDataModalArrayList = new ArrayList<>();
    NewsFeedListRecyclerViewAdapter newsFeedListRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);
        db = FirebaseFirestore.getInstance();
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withDragDistance(400)
                .withSavedState(savedInstanceState)
                .withRootViewScale(1f)
                .withRootViewYTranslation(4)
                .withMenuLayout(R.layout.root_nav_announcements)
                .withSavedState(savedInstanceState)
                .withContentClickableWhenMenuOpened(true)
                .inject();
        newsFeedList = (RecyclerView) findViewById(R.id.newsFeedList);
        announcementCatList = (RecyclerView) findViewById(R.id.categoryList);
        announcementListRecyclerViewAdapter = new AnnouncementListRecyclerViewAdapter(AnnouncementsAct.this, announcementCategoryDataModalArrayList);
        announcementCatList.setLayoutManager(new LinearLayoutManager(AnnouncementsAct.this));
        announcementCatList.setAdapter(announcementListRecyclerViewAdapter);

        db.collection("announcementCategory").orderBy("timestamp").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    announcementCategoryDataModalArrayList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                       Map<String,Object> data = document.getData();
                        AnnouncementCategoryDataModal announcementCategoryDataModal = new AnnouncementCategoryDataModal();
                        announcementCategoryDataModal.setKey(data.get(announcementCategoryDataModal.mKey).toString());
                        announcementCategoryDataModal.setCategoryDescription(data.get(announcementCategoryDataModal.mCategoryDescription).toString());
                        announcementCategoryDataModal.setCategoryName(data.get(announcementCategoryDataModal.mCategoryName).toString());
                        System.out.println(data.get(announcementCategoryDataModal.mCategoryName).toString());
                        announcementCategoryDataModalArrayList.add(announcementCategoryDataModal);
                    }
                    announcementListRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    System.out.println(task.getException());
                }
            }
        });
        newsFeedList.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        newsFeedListRecyclerViewAdapter = new NewsFeedListRecyclerViewAdapter(AnnouncementsAct.this,announcementNewsFeedDataModalArrayList);
        newsFeedList.setAdapter(newsFeedListRecyclerViewAdapter);


        db.collection("announcements").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    announcementNewsFeedDataModalArrayList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String,Object> data = document.getData();
                        AnnouncementNewsFeedDataModal model = new AnnouncementNewsFeedDataModal();
                        model.setAnnouncementCaption(data.get(model.mAnnouncementCaption).toString());
                        model.setAnnouncementDetails(data.get(model.mAnnouncementDetails).toString());
                        model.setKey(data.get(model.mKey).toString());
                      try {
                          model.setImagePath(data.get(model.mImagePath).toString());
                      }catch (NullPointerException e){

                      }
                        announcementNewsFeedDataModalArrayList.add(model);

                    }
                    newsFeedListRecyclerViewAdapter.notifyDataSetChanged();

                } else {
                    System.out.println(task.getException());
                }
            }
        });
        db.collection("announcements").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                announcementNewsFeedDataModalArrayList.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Map<String,Object> data = document.getData();
                    AnnouncementNewsFeedDataModal model = new AnnouncementNewsFeedDataModal();
                    model.setAnnouncementCaption(data.get(model.mAnnouncementCaption).toString());
                    model.setAnnouncementDetails(data.get(model.mAnnouncementDetails).toString());
                    model.setKey(data.get(model.mKey).toString());
                    try {
                        model.setImagePath(data.get(model.mImagePath).toString());
                    }catch (NullPointerException ex){

                    }
                    announcementNewsFeedDataModalArrayList.add(model);

                }
                newsFeedListRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        announcementListRecyclerViewAdapter.setOnItemClickListener(new AnnouncementListRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position, AnnouncementCategoryDataModal announcementCategoryDataModal) {
               slidingRootNav.closeMenu();
            }
        });
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(newsFeedList);
    }
}
