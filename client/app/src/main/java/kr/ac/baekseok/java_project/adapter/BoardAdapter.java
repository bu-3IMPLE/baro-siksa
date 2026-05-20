package kr.ac.baekseok.java_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.model.BoardPost;

import java.util.List;
import java.util.Locale;

/**
 * 게시판 RecyclerView 어댑터
 */
public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.PostViewHolder> {

    private final List<BoardPost> posts;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BoardPost post);
    }

    public BoardAdapter(List<BoardPost> posts, OnItemClickListener listener) {
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_board_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        BoardPost post = posts.get(position);
        holder.tvNo.setText(String.format(Locale.getDefault(), "%02d", post.getNo()));
        holder.tvSubject.setText(post.getSubject());
        holder.tvWriter.setText(post.getWriter());
        holder.tvReplyCount.setText(String.valueOf(post.getReplyCount()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(post);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvNo, tvSubject, tvWriter, tvReplyCount;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNo = itemView.findViewById(R.id.tv_no);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvWriter = itemView.findViewById(R.id.tv_writer);
            tvReplyCount = itemView.findViewById(R.id.tv_reply_count);
        }
    }
}
