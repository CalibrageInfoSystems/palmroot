package com.calibrage.palmroot.ui.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.github.chrisbanes.photoview.PhotoView;

import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.dbmodels.ViewVisitLog;
import com.calibrage.palmroot.utils.ImageUtility;

import java.util.ArrayList;
import java.util.List;

public class VisitLogRecyclerviewAdapter extends RecyclerView.Adapter<VisitLogRecyclerviewAdapter.ViewHolder> {

    public Context context;

  //  List<NurseryIrrigationLogXref> irrigationxref_list = new ArrayList<>();
    private List<ViewVisitLog> visitloglist = new ArrayList<>();
    private PopupWindow pwindo1,pwindo2;
    private Bitmap currentBitmap2 = null;
    String imagepath ;
    Boolean isCheck= true;
    public VisitLogRecyclerviewAdapter(Context context, List<ViewVisitLog> visitloglist) {
        this.context = context;
        this.visitloglist = visitloglist;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.viewvisitloglayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.nursername.setText(visitloglist.get(position).getNurseryname());
        holder.consignmentcode.setText(visitloglist.get(position).getCosignmentCode());
        holder.logtype.setText(visitloglist.get(position).getLogtype()+"");
        holder.ClientName.setText(visitloglist.get(position).getClientName());
        String beforeDate =  visitloglist.get(position).getLogDate();
        String finalDate =   CommonUtils.formateDateFromstring("yyyy-MM-dd", "dd-MM-yyyy", beforeDate);
        holder.logdate.setText(finalDate);
      //  holder.logdate.setText(visitloglist.get(position).getLogDate()+"");
    holder.Comments.setText(visitloglist.get(position).getComments());
      //  holder.ClientName.setText(irrigationxref_list.get(position).getDesc());

        holder.Comments.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          if (isCheck) {
                                              holder.Comments.setMaxLines(10);
                                              isCheck = false;
                                          } else {
                                              holder.Comments.setMaxLines(2);
                                              isCheck = true;
                                          }
                                      }
                                  });

        Log.e("=======>FileLocation",visitloglist.get(position).getFileLocation().length()+"");


        if( visitloglist.get(position).getFileLocation().length() != 0  ){
            holder.imagelinear.setVisibility(View.VISIBLE);
            holder.image.setImageResource(R.drawable.eye_icon);

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initiatePopupWindow1(position);

                }

            });
        }else{
            holder.imagelinear.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);

        }



    }

    private void initiatePopupWindow1(int position) {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.dialog_custom_layout,null);
            pwindo1 = new PopupWindow(layout, 650, 650, true);
            pwindo1.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView cancel =layout.findViewById(R.id.cancel);
            PhotoView photoView = layout.findViewById(R.id.imageView);

            if( visitloglist.get(position).getFileLocation()!=null) {
                Bitmap bitmap2 = BitmapFactory.decodeFile(visitloglist.get(position).getFileLocation());

                bitmap2 = ImageUtility.rotatePicture(90, bitmap2);
                Log.e("================>",bitmap2+"");
                currentBitmap2 = bitmap2;
                photoView.setImageBitmap(currentBitmap2);
            }
            else {
                photoView.setImageResource(R.drawable.no_camera);
            }

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pwindo1.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return visitloglist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nursername,consignmentcode,logtype, ClientName,logdate,Comments;
        LinearLayout imagelinear;
ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nursername = (TextView )itemView.findViewById(R.id.nurseryname);
            this.consignmentcode = (TextView )itemView.findViewById(R.id.consignmentcode);
            this.logtype = (TextView )itemView.findViewById(R.id.logtype);
            this.ClientName = (TextView )itemView.findViewById(R.id.ClientName);
            this.logdate =(TextView) itemView.findViewById(R.id.logdate);
            this.Comments =(TextView) itemView.findViewById(R.id.Comments);
            this.image =(ImageView) itemView.findViewById(R.id.image);
            imagelinear = (LinearLayout ) itemView.findViewById(R.id.imagelinear);
        }
    }
}
