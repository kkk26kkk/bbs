package com.kkk26kkk.bbs.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import com.kkk26kkk.bbs.service.UserService;
import com.kkk26kkk.common.model.Path;

@Controller
public class ArticleController {
	@Autowired
	private ArticleService articleService;
	@Autowired
	private UserService userService;
	@Autowired
	private CommentService commentService;
	
	// �� �󼼺���
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.GET)
	String show(@PathVariable int articleId, Model model) {
		Article article = articleService.getArticle(articleId);
		
		model.addAttribute("article", article.showArticle());
		
		// TODO �𵨿� �㵵�� �����ϰ�, �信���� ���� - �� ��ư���� �󼼺��� �������� �����ְ�, Article ��ü�ʹ� ��� ���� ���ٰ� �� ���� ����
//		dto.setUpdateFormLink(Path.UpdateForm.getPath() + "?articleId=" + getArticleId());
//		dto.setReplyFormLink(Path.ReplyForm.getPath() + "?articleId=" + getArticleId());
//		dto.setDeleteLink(Path.Delete.getPath());
//		dto.setCommentLink(Path.Comment.getPath());
		
		return "/board/show";
	}
	
	// �� �ۼ� ��
	// TODO Ŀ���� �������̽��� ������Ʈ - @LoginRequire - user���� isLogin üũ
	@RequestMapping(value = {"/board/write", "/board/reply"})
	String writeForm(HttpServletRequest request, Model model, @RequestParam(required = false) int articleId/*, User user*/) {
		// TODO user ��ü�� ���⿡�� �Ǵ��ϴ� ���� �ƴ�, ���ͼ��Ϳ��� �������ֵ��� (���:��ü)
		String userId = request.getSession().getAttribute("userId").toString();
		User user = userService.getUser(userId);
		
		ArticleDto articleDto = user.createArticle();
		
		if(Path.ReplyForm.comparePath(request.getRequestURI())) {
			ArticleDto article = articleService.getArticleDto(articleId);
			
			articleDto.setArticleId(article.getArticleId());
			articleDto.setTitle(article.getTitle());
		}
		
		model.addAttribute("article", articleDto);
//		articleDto = null;
		model.addAttribute("path", Path.Article.getPath());
		
		return "/board/write";
	}
	
	// �� ���� ��
	@RequestMapping(value = "/board/update")
	String updateForm(@RequestParam int articleId, Model model) {
		ArticleDto article = articleService.getArticleDto(articleId);
		
		model.addAttribute("article", article);
		
		return "/board/update";
	}
	
	// �� �亯 ��
//	@RequestMapping(value = "/board/reply")
//	String replyForm(@RequestParam int articleId, Model model, HttpServletRequest request) {
//		String userName = request.getSession().getAttribute("userName").toString();
//		
//		ArticleDto article = articleService.getArticleDto(articleId); // ������ ������
//		
//		
//		return "/board/reply";
//	}
	
	// �� ��� ó��
	@RequestMapping(value = "/board", method = RequestMethod.POST)
	@ResponseBody
	Map<String, Object> write(@RequestBody ArticleDto articleDto, HttpServletRequest request) {
		// TODO user ��ü�� ���⿡�� �Ǵ��ϴ� ���� �ƴ�, ���ͼ��Ϳ��� �������ֵ��� (���:��ü)
		String userId = request.getSession().getAttribute("userId").toString();
		User user = userService.getUser(userId);
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(false == user.isUserId(articleDto.getUserId())) {
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
	@RequestMapping(value = "/board", method = RequestMethod.PUT)
	@ResponseBody
	Map<String, Object> update(@RequestBody Article article) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			articleService.updateArticle(article);
			result.put("code", HttpStatus.OK);
			result.put("redirect", Path.Article.getPath() + "/" + article.getArticleId());
		} catch(Exception e) {
			result.put("msg", "�ش� ���� �������� �ʽ��ϴ�.");
            result.put("code", HttpStatus.NOT_FOUND);
            e.printStackTrace();
		}
		
		return result;
	}
	
	// �� ���� ó�� // TODO /{articleId} �ٿ��� ������ ����
	@RequestMapping(value = "/board", method = RequestMethod.DELETE)
	@ResponseBody
	Map<String, Object> remove(@RequestBody Article article) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			articleService.deleteArticle(article.getArticleId());
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
	@ResponseBody List<CommentDto> getCommentList(@RequestParam int articleId, HttpServletRequest request) {
		List<Comment> list = commentService.getCommentList(articleId);
		
		List<CommentDto> commentList = list.stream()
				.map(Comment::showComment)
				.collect(Collectors.toList());
		
		return commentList;
	}
}
