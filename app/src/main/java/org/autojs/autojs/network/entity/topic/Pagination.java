
package org.autojs.autojs.network.entity.topic;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Pagination {

    @SerializedName("currentPage")
    private Long mCurrentPage;
    @SerializedName("next")
    private Page mNext;
    @SerializedName("pageCount")
    private Long mPageCount;
    @SerializedName("pages")
    private List<Object> mPages;
    @SerializedName("prev")
    private Page mPrev;
    @SerializedName("rel")
    private List<Object> mRel;

    public Long getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(Long currentPage) {
        mCurrentPage = currentPage;
    }

    public Page getNext() {
        return mNext;
    }

    public void setNext(Page next) {
        mNext = next;
    }

    public Long getPageCount() {
        return mPageCount;
    }

    public void setPageCount(Long pageCount) {
        mPageCount = pageCount;
    }

    public List<Object> getPages() {
        return mPages;
    }

    public void setPages(List<Object> pages) {
        mPages = pages;
    }

    public Page getPrev() {
        return mPrev;
    }

    public void setPrev(Page prev) {
        mPrev = prev;
    }

    public List<Object> getRel() {
        return mRel;
    }

    public void setRel(List<Object> rel) {
        mRel = rel;
    }

}
