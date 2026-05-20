package kr.ac.baekseok.java_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;

import java.util.List;

/**
 * 가게 상세 화면의 이미지 슬라이드용 ViewPager2 어댑터
 */
public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder> {

    private final List<Integer> imageResIds;

    public ImageCarouselAdapter(List<Integer> imageResIds) {
        this.imageResIds = imageResIds;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 단순한 ImageView 래퍼를 코드로 생성한다
        FrameLayout container = new FrameLayout(parent.getContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        container.setBackgroundResource(R.color.placeholder_bg);

        ImageView imageView = new ImageView(parent.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = android.view.Gravity.CENTER;

        // dp -> px 변환
        float density = parent.getContext().getResources().getDisplayMetrics().density;
        int sizePx = (int) (60 * density);
        params.width = sizePx;
        params.height = sizePx;
        imageView.setLayoutParams(params);
        imageView.setImageResource(R.drawable.ic_image_placeholder);

        container.addView(imageView);
        return new ImageViewHolder(container, imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Integer resId = imageResIds.get(position);
        if (resId != null && resId != 0) {
            // 실제 이미지가 있을 때
            holder.imageView.setImageResource(resId);
            // 가운데 크기 작게 → match_parent로 변경
            ViewGroup.LayoutParams lp = holder.imageView.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.imageView.setLayoutParams(lp);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        // resId가 0이면 placeholder 그대로
    }

    @Override
    public int getItemCount() {
        return imageResIds == null ? 0 : imageResIds.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView, ImageView imageView) {
            super(itemView);
            this.imageView = imageView;
        }
    }
}
