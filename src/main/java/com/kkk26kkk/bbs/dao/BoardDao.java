package com.kkk26kkk.bbs.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kkk26kkk.bbs.model.Article;
import com.kkk26kkk.common.model.PageListParam;
import com.kkk26kkk.common.model.PageList;

@Repository
public class BoardDao extends BaseDao {
	@Autowired
	SqlSessionTemplate sqlSession;
	
	// TODO Page ��ü�� ������������ ����� -> page, pageSize -> start, end�� get �� �־ �� ��?
	
	// TODO List�� ������ ��ü ����� -> list, page, pageSize, totalCount, totalPage, hasNext, totalPage(getter)
	
	public PageList<Article> getArticleList(PageListParam pageDto) { /** totalPage **/
		return super.getPageListTotal("getArticleList", "getArticleCount", pageDto);
	}
	
	public int getArticleCount() {
		return sqlSession.selectOne("getArticleCount");
	}
	
	// TODO PageDao ���� �� ����� -> selectList �� ���ο��� �ϴ� -> selectPageList ���� �޼ҵ� ������ش�
	
	public PageList<Article> getArticleListMore(int page) { /** hasNext **/
//		return PageDao.selectPageList("getArticleList", page); �ᱹ�̰� -> ������ PageList<Article>
		
		
//		PageList<Article> pageList = super.getArticleListMore("getArticleList", page);
		
		
		/**
		 * 
		page.setEndNum(page.getEndNum() + 1);
		
		List<Article> list = sqlSession.selectList("getArticleList", page);
		
		boolean hasNext = false;
		if(page.getPageSize() < list.size()) {
			hasNext = true;
		}
		
		if(hasNext) {
			list.remove(page.getPageSize());
		}
		
		// TODO hasNext �� ������
		
		return list;
		
		*/
		
		return null;
	}
	
}
