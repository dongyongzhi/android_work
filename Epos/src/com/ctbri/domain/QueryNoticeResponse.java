package com.ctbri.domain;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * 公告或图片返回
 * @author qin
 * 
 * 2012-12-14
 */
public class QueryNoticeResponse extends ElecResponse  implements Parcelable{

	/**  */
	private static final long serialVersionUID = -1275400454722381161L;
	private String noticInfo;
	
	private Notice[] notices;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		 dest.writeString(noticInfo);
	}
	
	public static final Parcelable.Creator<QueryNoticeResponse> CREATOR = new Parcelable.Creator<QueryNoticeResponse>() {

		@Override
		public QueryNoticeResponse createFromParcel(Parcel source) {
			QueryNoticeResponse resp = new QueryNoticeResponse();
			resp.noticInfo = source.readString();
			return resp;
		}

		@Override
		public QueryNoticeResponse[] newArray(int size) {
			return new QueryNoticeResponse[size];
		}
	};
 

	public Notice[] getNotices() {
		return notices;
	}

	public void setNotices(Notice[] notices) {
		this.notices = notices;
	}


	public void setNoticInfo(String noticInfo) {
		this.noticInfo = noticInfo;
	}

	public String getNoticInfo() {
		return noticInfo;
	}


	public static class Notice  implements Parcelable{
		private String noticeInfo;
		private String type;
		
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(noticeInfo);
			dest.writeString(type);
		}
		public static final Parcelable.Creator<Notice> CREATOR = new Parcelable.Creator<Notice>() {
			@Override
			public Notice createFromParcel(Parcel source) {
				Notice notice = new Notice();
				notice.noticeInfo = source.readString();
				notice.type  = source.readString();
				return notice;
			}

			@Override
			public Notice[] newArray(int size) {
				return new Notice[size];
			}
		};
		
		public String getNoticeInfo() {
			return noticeInfo;
		}
		public void setNoticeInfo(String noticeInfo) {
			this.noticeInfo = noticeInfo;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}

	}
}
