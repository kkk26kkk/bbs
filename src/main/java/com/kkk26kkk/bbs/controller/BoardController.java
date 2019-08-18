package com.kkk26kkk.bbs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kkk26kkk.bbs.model.Article;
import com.kkk26kkk.bbs.model.ArticleDto;
import com.kkk26kkk.bbs.model.ArticleParam;
import com.kkk26kkk.bbs.model.Comment;
import com.kkk26kkk.bbs.model.CommentParam;
import com.kkk26kkk.bbs.model.User;
import com.kkk26kkk.bbs.service.BoardService;
import com.kkk26kkk.common.model.PageList;
import com.kkk26kkk.common.model.Path;

@Controller
public class BoardController {
	@Autowired
	private BoardService boardService;
//	@Autowired
//	private Environment environment;
	
	private static final int pageSize = 10;

	@RequestMapping(value = "/board/list", method = RequestMethod.GET)
	String showBoard(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String sort) {
//		int length = 0;
//		for(Article board : boardList) {
//			if(null == board.getContents()) {
//				continue;
//			}
//			board.showContents();
//			if(10 == ++length) {
//				break;
//			}
//		}

		ArticleParam articleParam = new ArticleParam
				.Builder(pageSize)
				.useTotal(true)
				.useMore(true)
				.sort(sort)
				.build();
		
	    PageList<Article> pageList = boardService.getArticleList(articleParam);
	    articleParam = null;
	    
		List<Article> articleList = pageList.getList();
		int totalPage = pageList.getTotalPage();
		int totalCount = pageList.getTotalCount();
		boolean hasNext = pageList.hasNext();
		
		List<ArticleDto> boardContents = articleList.stream()
				.map(Article::showHeader)
//				.filter(v -> null == v.getContents())
//				.limit(10)
				.collect(Collectors.toList());

		model.addAttribute("writeFormLink", Path.WriteForm.getPath());
		model.addAttribute("boardContents", boardContents);
		model.addAttribute("page", page);
		
		model.addAttribute("hasNext", hasNext);
		
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalCount", totalCount);
		
		return "/board/articleList";
	}
	
	@RequestMapping(value = "/board/showMore", method = RequestMethod.GET)
	@ResponseBody
	Map<String, Object> showMore(@RequestParam int page, @RequestParam String sort) {
		Map<String, Object> map = new HashMap<>();
		
		ArticleParam articleParam = new ArticleParam
				.Builder(pageSize)
				.build();
		
		List<Article> articleList = boardService.getArticleListMore(articleParam);
		List<ArticleDto> boardContents = articleList.stream()
				.map(Article::showHeader)
				.collect(Collectors.toList());
		
		map.put("boardContents", boardContents);
		map.put("page", page);
		
		return map;
	}
	
	@RequestMapping(value = "/board/feedList", method = RequestMethod.GET)
	String feedList(Model model, @RequestParam(defaultValue = "0") int page, User user) {
		ArticleParam articleParam = new ArticleParam
				.Builder(pageSize)
				.useTotal(true)
//				.useMore(true)
//				.sort(sort)
				.build();
		
        // XXX �۸���Ʈ, ��۸���Ʈ�� service�ܿ��� ���� ó���ؾ� �ұ��?
        // TODO �۸���Ʈ�� ���񽺿��� DAO ���� ȣ���ϰ�, �ۺ� ��۸���Ʈ�� ���̴� AOP ���� (1��, List, PageList ����)
		PageList<Article> pageArticleList = boardService.getFeedList(articleParam); 
		List<Article> articleList = pageArticleList.getList();
		
        String articleIdList = articleList.stream()
        // TODO Article�� getArticleId() �������̵�, super.getArticleId() ȣ��
			.map(Article::getArticleId) // XXX getArticleId() -> public���� ����������� �������?
			.collect(Collectors.joining(","));
		
        // XXX totalPage ������� �Ϸ��� totalCount ó���� ��� �ؾ��ұ��?
        // TODO Param���� useMore(true) ���� useTotal(true) ��� -> PageList���� getTotalPage() ���
		CommentParam commentParam = new CommentParam
				.Builder(5, articleIdList)
				.useMore(true)
				.userId(user.getUserId())
				.build();
		
		PageList<Comment> pageCommentList = boardService.getFeedCommentList(commentParam);
		
		// XXX ��� ����Ʈ���� �ش� �� ����Ʈ�� ¦�� �������� ��� �ؾ��ұ��?
        // TODO PageList<Comment> ��ȸ�� ��ü�� getArticleId() �������� �׷��� (Comment�� �������̵� �ʿ�, �׷���� �ܺ����� �ʿ�)
        // Map<String, PageList<Comment>> -> ���� Map�� key�� �������� CommentDto�� ArticleDto�� �׷���
        // ArticleDto�� PageList<CommentDto> �߰� �Ǵ� Map���� ArticleDto�� PageList<CommentDto> ����
        // ���丮 ��ü �߰� ��� -> PageList<Article>�� PageList<Comment>�� ���� PageList<ArticleDto>�� PageList<CommentDto>�� ��ȯ�ϴ�
        // VO�� DTO�� �ʵ屸���� ���� �޶����� �� ���� ����, ���� �ʵ���� ����Ͻ� ������ ���� ���� ���� �Ǿ��� ���� ���� ����

		return "/board/feedList";
	}
}
