package com.techturbo.bluetooth.light.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.techturbo.bluetooth.light.model.Constants;
import com.techturbo.bluetooth.light.model.Light;
import com.techturbo.bluetooth.light.model.Lights;
import com.techturbo.okhttputils.okhttp.model.MeshInfo;
import com.techturbo.okhttputils.okhttp.model.MessageEvent;
import com.techturbo.okhttputils.okhttp.model.SystemConfig;
import com.techturbo.ttmeslight.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MeshInfoActivity extends Activity {

    ListView listView;
    private List<MeshInfo> meshInfoLists;
    private LayoutInflater inflater;
    private MeshListAdapter adapter;
    private ImageView addButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        setContentView(R.layout.activity_mesh_info);

        addButton = (ImageView)this.findViewById(R.id.img_header_menu_right);
        backButton = (ImageButton)this.findViewById(R.id.img_header_menu_left);

        meshInfoLists = SystemConfig.shareConfig().readMeshInfos();

        this.adapter = new MeshListAdapter();
        this.listView = (ListView)this.findViewById(R.id.meshListView);
        this.listView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SystemConfig.shareConfig().saveMeshInfo(meshInfoLists);

                MeshInfoActivity.this.finish();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MeshInfo info = new MeshInfo();

                info.setId(java.util.UUID.randomUUID().toString());
                info.setMeshName("");
                info.setMeshPassword("");

                adapter.add(info);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void deleteMesh(final MeshInfo e) {
        String msg = "Delete this mesh info?";
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MeshInfoActivity.this);
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("Detele",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        meshInfoLists.remove(e);
                        SystemConfig.shareConfig().deleteMeshInfo(e);
                        adapter.notifyDataSetChanged();
                    }
                });
        normalDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private static class MeshInfoItemHolder {
        public ImageView deleteIcon;
        public ImageView SelectIcon;
        public EditText nameET;
        public EditText pwdET;
    }

    final class MeshListAdapter extends BaseAdapter {

        public MeshListAdapter() {

        }

        @Override
        public int getCount() {
            return meshInfoLists.size();
        }

        @Override
        public MeshInfo getItem(int position) {
            return meshInfoLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MeshInfoActivity.MeshInfoItemHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.mesh_item, null);

                ImageView deleteIcon = (ImageView) convertView
                        .findViewById(R.id.meshDeleteIcon);
                ImageView SelectIcon = (ImageView) convertView
                        .findViewById(R.id.meshSelectIcon);
                EditText nameET = (EditText) convertView
                        .findViewById(R.id.meshNameET);
                EditText pwdET = (EditText) convertView
                        .findViewById(R.id.pwdNameET);

                holder = new MeshInfoActivity.MeshInfoItemHolder();

                holder.deleteIcon = deleteIcon;
                holder.SelectIcon = SelectIcon;
                holder.nameET = nameET;
                holder.pwdET = pwdET;

                convertView.setTag(holder);
            } else {
                holder = (MeshInfoActivity.MeshInfoItemHolder) convertView.getTag();
            }

            final MeshInfo mesh = this.getItem(position);

            holder.nameET.setText(mesh.getMeshName());
            holder.pwdET.setText(mesh.getMeshPassword());

            holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteMesh(mesh);
                }
            });

            holder.SelectIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SystemConfig.shareConfig().saveDefaultMeshInfo(mesh);

                    SystemConfig.shareConfig().saveMeshInfo(meshInfoLists);
                    EventBus.getDefault().post(new MessageEvent(Constants.Event_Re_Scan));

                    MeshInfoActivity.this.finish();
                }
            });

            holder.nameET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!"".equals(s.toString().trim())) {
                        mesh.setMeshName(s.toString());
                    }
                }
            });

            holder.pwdET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!"".equals(s.toString().trim())) {
                        mesh.setMeshPassword(s.toString());
                    }
                }
            });

            return convertView;
        }

        public void del(MeshInfo info) {
            meshInfoLists.remove(info);
            SystemConfig.shareConfig().deleteMeshInfo(info);
        }

        public void add(MeshInfo info) {
            meshInfoLists.add(info);
        }

        public Light get(int meshAddress) {
            return Lights.getInstance().getByMeshAddress(meshAddress);
        }
    }
}
