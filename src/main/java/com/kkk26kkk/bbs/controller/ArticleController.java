package com.kkk26kkk.bbs.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kkk26kkk.bbs.model.Article;
import com.kkk26kkk.bbs.model.ArticleDto;
import com.kkk26kkk.bbs.model.Comment;
import com.kkk26kkk.bbs.model.CommentDto;
import com.kkk26kkk.bbs.model.User;
import com.kkk26kkk.bbs.service.ArticleService;
import com.kkk26kkk.bbs.service.CommentService;
import com.kkk26kkk.common.model.Code;
import com.kkk26kkk.common.model.PageListParam;
import com.kkk26kkk.common.model.Path;

@Controller
public class ArticleController {
	@Autowired
	private ArticleService articleService;
	@Autowired
	private CommentService commentService;
	
	private static final int pageSize = 10;
	
	// �� �󼼺���
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.GET)
	String show(@PathVariable int articleId, Model model) {
		Article article = articleService.getArticle(articleId);
		
		model.addAttribute("article", article.showContent());
		
		model.addAttribute("updateFormLink", Path.UpdateForm.getPath() + "?articleId=" + articleId);
		model.addAttribute("replyFormLink", Path.ReplyForm.getPath() + "?articleId=" + articleId);
		model.addAttribute("deleteLink", Path.Article.getPath() + "/" + articleId);
		model.addAttribute("commentLink", Path.Comment.getPath());
		
		return "/board/show";
	}
	
	// �� �ۼ� ��
	// Ŀ���� �������̽��� ������Ʈ - @LoginRequire - user���� isLogin üũ
	@RequestMapping(value = {"/board/write", "/board/reply"}) // PathVariable
	String writeForm(HttpServletRequest request, Model model, @RequestParam(defaultValue = "0") int articleId, User user) {
		ArticleDto articleDto = user.createArticle();
		System.out.println("userId" + user.getUserId());
		
		if(Path.ReplyForm.compare(request.getRequestURI())) {
			ArticleDto article = articleService.getArticleDto(articleId);
			
			articleDto.setArticleId(article.getArticleId());
			articleDto.setTitle("RE:" + article.getTitle());
		} 
		
		model.addAttribute("article", articleDto);
//		articleDto = null;
		model.addAttribute("path", Path.Article.getPath());
		
		return "/board/write";
	}
	
	// �� ���� ��
	@RequestMapping(value = "/board/update")
	String updateForm(@RequestParam int articleId, Model model) {
		ArticleDto articleDto = articleService.getArticleDto(articleId);
		
		model.addAttribute("article", articleDto);
		
		return "/board/update";
	}
	
	// �� ��� ó��
	@RequestMapping(value = "/board", method = RequestMethod.POST)
	@ResponseBody
	Map<String, Object> write(HttpServletRequest request, @RequestBody ArticleDto articleDto, User user) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(!user.isUserId(articleDto.getUserId())) {
			result.put("msg", "�α��� ������ �ٸ��ϴ�.");
            result.put("code", HttpStatus.FORBIDDEN);
		}
		
		try {
			articleService.insertArticle(articleDto, user);
			result.put("code", HttpStatus.OK);
			result.put("redirect", Path.ArticleList.getPath());
		} catch(Exception e) {
			result.put("msg", e.getMessage());
            result.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
		}
		
		return result;
	}
	
	// �� ���� ó�� // TODO /{articleId} �ٿ��� ������ ����
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.PUT)
	@ResponseBody
	Map<String, Object> update(@PathVariable int articleId, @RequestBody ArticleDto articleDto, User user) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			// TODO �ڱ�۸� ������ �� �ֵ��� user �޾Ƽ� ���� - �ڿ� ������ ����
			articleService.updateArticle(articleId, articleDto);
			result.put("code", HttpStatus.OK);
			result.put("redirect", Path.Article.getPath() + "/" + articleId);
		} catch(SQLException e) { // TODO �̷� ������, ���񽺿��� ���� �������� ����ش�
			result.put("msg", e.getMessage());
            result.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
		} catch(Exception e) {
			result.put("msg", "�ش� ���� �������� �ʽ��ϴ�.");
            result.put("code", HttpStatus.NOT_FOUND);
            e.printStackTrace();
		}
		
		return result;
	}
	
	// �� ���� ó�� // TODO /{articleId} �ٿ��� ������ ����
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.DELETE)
	@ResponseBody
	Map<String, Object> remove(@PathVariable int articleId, @RequestBody Article article, User user) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		// TODO �� �ۼ��ڸ� ������ �� �ֵ���
		
		try {
			articleService.deleteArticle(articleId);
			result.put("code", HttpStatus.OK);
			result.put("redirect", Path.ArticleList.getPath());
		} catch(Exception e) {
			result.put("msg", "�ش� ���� �������� �ʽ��ϴ�.");
            result.put("code", HttpStatus.NOT_FOUND);
            e.printStackTrace();
		}
		
		return result;
	}
	
	// ��� ���
	@RequestMapping(value = "/comment", method = RequestMethod.POST)
	@ResponseBody
	Map<String, Object> comment(@RequestBody Comment comment, HttpServletRequest request) {
		comment.setUserId(request.getSession().getAttribute("userId").toString());
		comment.setUserName(request.getSession().getAttribute("userName").toString());
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			commentService.insertComment(comment);
			result.put("code", HttpStatus.OK);
			result.put("userName", comment.getUserName());
			result.put("contents", comment.getContents());
		} catch(Exception e) {
			result.put("msg", "�ش� ���� �������� �ʽ��ϴ�.");
            result.put("code", HttpStatus.NOT_FOUND);
            e.printStackTrace();
		}
		
		return result;
	}
	
	// ��� ����Ʈ
	@RequestMapping(value = "/comment", method = RequestMethod.GET)
	@ResponseBody List<CommentDto> getCommentList(@RequestParam int articleId, @RequestParam(defaultValue = "1") int page, HttpServletRequest request, User user) {
		// TODO ��� ����¡ ó��
		// TODO service�ܿ��� ó���� �κ� �̵�
		
		PageListParam pageListParam = new PageListParam
				.Builder(page, pageSize)
				.useTotal(true)
				.useMore(true)
				.build();
		
		List<Comment> list = commentService.getCommentList(articleId);
		list.stream()
			.filter(comment -> /* !(comment.isCommentWriter(user.getUserId() 
									|| article.isArticleWriter(user.getUserId())))
									&& */ Code.COMMENT_SECRET_TYPE_PRIVATE.compare(comment.getCode()))
			.forEach(comment -> comment.setContents("��� ����Դϴ�."));
		list.stream()
			.filter(comment -> /* TODO !"������".equals(user.getGrade()) && */ Code.COMMENT_SECRET_TYPE_REPORTED.compare(comment.getCode()))
			.forEach(comment -> comment.setContents("�Ű� ������ ����Դϴ�."));
		
		List<CommentDto> commentList = list.stream()
				.map(Comment::showComment)
				.collect(Collectors.toList());
		
		return commentList;
	}
}
