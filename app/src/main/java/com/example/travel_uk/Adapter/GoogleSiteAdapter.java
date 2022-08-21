package com.example.travel_uk.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_uk.GoogleSite;
import com.example.travel_uk.NearSite;
import com.example.travel_uk.R;
import com.example.travel_uk.databinding.SiteItemLayoutBinding;


import java.util.List;

public class GoogleSiteAdapter extends RecyclerView.Adapter<GoogleSiteAdapter.ViewHolder> {

    private List<GoogleSite> googleSites;
    private NearSite nearSite;

    public GoogleSiteAdapter(NearSite nearSite) {
        this.nearSite = nearSite;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SiteItemLayoutBinding siteItemLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.site_item_layout,parent,false);

        return new ViewHolder(siteItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (googleSites != null){
            GoogleSite site = googleSites.get(position);
            holder.siteItemLayoutBinding.setGoogleSite(site);
            holder.siteItemLayoutBinding.setListener(nearSite);
        }
    }

    @Override
    public int getItemCount() {
        if (googleSites != null)
            return googleSites.size();
        else
            return 0;
    }

    public void setGoogleSites(List<GoogleSite> googleSites) {
        this.googleSites = googleSites;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private SiteItemLayoutBinding siteItemLayoutBinding;

        public ViewHolder(@NonNull SiteItemLayoutBinding siteItemLayoutBinding) {
            super(siteItemLayoutBinding.getRoot());
            this.siteItemLayoutBinding = siteItemLayoutBinding;
        }
    }
}
