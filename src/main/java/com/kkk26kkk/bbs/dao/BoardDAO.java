package com.kkk26kkk.bbs.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kkk26kkk.bbs.model.Article;
import com.kkk26kkk.common.model.Page;

@Repository
public class BoardDao {
	@Autowired
	SqlSessionTemplate sqlSession;
	
	// TODO Page ��ü�� ������������ ����� -> page, pageSize -> start, end�� get �� �־ �� ��?
	
	// TODO List�� ������ ��ü ����� -> list, page, pageSize, totalCount, totalPage, hasNext, totalPage(getter)
	
	public List<Article> getArticleList(Page page) {
		return sqlSession.selectList("getArticleList", page);
	}
	
	public int getArticleCount() {
		return sqlSession.selectOne("getArticleCount");
	}
	
	// TODO PageDao ���� �� ����� -> selectList �� ���ο��� �ϴ� -> selectPageList ���� �޼ҵ� ������ش�
	
	public List<Article> getArticleListMore(Page page) {
//		return PageDao.selectPageList("getArticleList", page); �ᱹ�̰� -> ������ PageList<Article>
		
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
	}
	
}
