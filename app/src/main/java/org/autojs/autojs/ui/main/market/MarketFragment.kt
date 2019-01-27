package org.autojs.autojs.ui.main.market

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.stardust.autojs.workground.WrapContentLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_market.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.autojs.autojs.R
import org.autojs.autojs.network.TopicService
import org.autojs.autojs.network.entity.topic.Topic
import org.autojs.autojs.ui.main.ViewPagerFragment
import org.autojs.autojs.ui.widget.AvatarView
import org.joda.time.format.DateTimeFormat

class MarketFragment : ViewPagerFragment(0) {

    private val mTopics = ArrayList<Topic>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_market, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topicsView.layoutManager = WrapContentLinearLayoutManager(context)
        topicsView.adapter = Adapter()
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        refresh()
    }

    private fun refresh() {
        GlobalScope.launch(Dispatchers.Main) {
            swipeRefreshLayout.isRefreshing = true
            val topics = TopicService.getScriptsTopics()
            mTopics.clear()
            mTopics.addAll(topics)
            topicsView.adapter!!.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onBackPressed(activity: Activity): Boolean {
        return false
    }

    override fun onFabClick(fab: FloatingActionButton) {

    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val rootView: TextView = itemView.findViewById(R.id.root)
        val titleView: TextView = itemView.findViewById(R.id.title)
        val avatarView: AvatarView = itemView.findViewById(R.id.avatar)
        val usernameView: TextView = itemView.findViewById(R.id.username)
        val dateView: TextView = itemView.findViewById(R.id.date)

    }

    inner class Adapter : RecyclerView.Adapter<TopicViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
            return TopicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false))
        }

        override fun getItemCount(): Int {
            return mTopics.size
        }

        override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {

            val topic = mTopics[position]
            holder.run {
                val root = topic.appInfo.permissions.contains("root")
                rootView.text = if (root) {
                    "Root"
                } else {
                    "免Root"
                }
                titleView.text = topic.title
                avatarView.setUser(topic.user)
                usernameView.text = topic.user.username

                dateView.text = DateTimeFormat.mediumDateTime().print(topic.timestamp.toLong())
            }
        }

    }
}