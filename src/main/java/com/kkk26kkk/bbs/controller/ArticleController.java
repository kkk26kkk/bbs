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
import com.kkk26kkk.bbs.model.CommentParam;
import com.kkk26kkk.bbs.model.Comment;
import com.kkk26kkk.bbs.model.CommentDto;
import com.kkk26kkk.bbs.model.User;
import com.kkk26kkk.bbs.service.ArticleService;
import com.kkk26kkk.bbs.service.CommentService;
import com.kkk26kkk.common.model.Code;
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
	String show(@PathVariable String articleId, Model model) {
		Article article = articleService.getArticle(articleId);
		
		model.addAttribute("article", article.showContent());
		
		model.addAttribute("updateFormLink", Path.Board.getPath() + "/" + articleId + "/update");
		model.addAttribute("replyFormLink", Path.Board.getPath() + "/" + articleId + "/reply");
		model.addAttribute("deleteLink", Path.Board.getPath() + "/" + articleId);
		model.addAttribute("commentLink", Path.Comment.getPath());
		
		return "/board/show";
	}
	
	// �� �ۼ� ��
	// Ŀ���� �������̽��� ������Ʈ - @LoginRequire - user���� isLogin üũ
	@RequestMapping(value = {"/board/write", "/board/{articleId}/reply"})
	String writeForm(HttpServletRequest request, Model model, @PathVariable String articleId, User user) {
		ArticleDto articleDto = user.createArticle();
		
//		String replyForm = Path.Article.getPath() + "/" + articleId + "/reply"; 
		
		if(Path.ReplyForm.���ԵǴٿ����ʰſ�(request.getRequestURI())) {
			Article article = articleService.getArticle(articleId);
			ArticleDto parentArticleDto = article.showContent();
			
			articleDto.setArticleId(parentArticleDto.getArticleId());
			articleDto.setTitle("RE:" + parentArticleDto.getTitle());
		}
		
		model.addAttribute("article", articleDto);
//		articleDto = null;
		model.addAttribute("path", Path.Board.getPath());
		
		return "/board/write";
	}
	
	// �� ���� ��
	@RequestMapping(value = "/board/{articleId}/update")
	String updateForm(@PathVariable String articleId, Model model) {
		Article article = articleService.getArticle(articleId);
		ArticleDto articleDto = article.showContent();
		
		model.addAttribute("article", articleDto);
		
		return "/board/update";
	}
	
	// �� ��� ó��
	@RequestMapping(value = "/board", method = RequestMethod.POST)
	@ResponseBody
	Map<String, Object> write(HttpServletRequest request, @RequestBody ArticleDto articleDto, User user) {
		Map<String, Object> result = new HashMap<String, Object>();
		
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
	
	// �� ���� ó��
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.PUT)
	@ResponseBody
	Map<String, Object> update(@PathVariable String articleId, @RequestBody ArticleDto articleDto, User user) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(!user.isUserId(articleDto.getUserId())) {
			result.put("msg", "�α��� ������ �ٸ��ϴ�.");
            result.put("code", HttpStatus.FORBIDDEN);
		}
		
		try {
			articleService.updateArticle(articleId, articleDto);
			result.put("code", HttpStatus.OK);
			result.put("redirect", Path.Board.getPath() + "/" + articleId);
		} catch(SQLException e) {
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
	
	// �� ���� ó��
	@RequestMapping(value = "/board/{articleId}", method = RequestMethod.DELETE)
	@ResponseBody
	Map<String, Object> remove(@PathVariable String articleId, @RequestBody ArticleDto articleDto, User user) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(!user.isUserId(articleDto.getUserId())) {
			result.put("msg", "�α��� ������ �ٸ��ϴ�.");
            result.put("code", HttpStatus.FORBIDDEN);
		}
		
		try {
			articleService.deleteArticle(articleId);
			result.put("code", HttpStatus.OK);
			result.put("redirect", Path.ArticleList.getPath());
		}/* catch(BizException e) { // Ŀ���� ���� ���� -> updateArticleExample
			result.put("msg", e.getMessage());
            result.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
		}*/ catch(Exception e) {
			result.put("msg", "�ش� ���� �������� �ʽ��ϴ�.");
            result.put("code", HttpStatus.NOT_FOUND);
            e.printStackTrace();
		}
		
		return result;
	}
	
	// ��� ���
	@RequestMapping(value = "/board/comment", method = RequestMethod.POST)
	@ResponseBody
	Map<String, Object> comment(@RequestBody CommentDto commentDto, HttpServletRequest request, User user) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			int resultCommentId = commentService.insertComment(commentDto, user);
			// XXX ��� ��� �� ��ܿ���  ��ϵ� ����� �ǽð����� ǥ��Ƿ��� ��� �ؾ� �ұ��?
			Comment comment = commentService.getComment(resultCommentId);			
			result.put("comment", comment.showContent());
			result.put("code", HttpStatus.OK);
		} catch(Exception e) {
			result.put("msg", "�ش� ���� �������� �ʽ��ϴ�.");
            result.put("code", HttpStatus.NOT_FOUND);
            e.printStackTrace();
		}
		
		return result;
	}
	
	// ��� ����Ʈ
	@RequestMapping(value = "/board/comment", method = RequestMethod.GET) // TODO @PathVariable
	@ResponseBody List<CommentDto> getCommentList(@RequestParam String articleId, @RequestParam(defaultValue = "0") int page, HttpServletRequest request, User user) {
		CommentParam commentParam = new CommentParam
				.Builder(pageSize, articleId)
				.useTotal(true)
				.useMore(true)
				.userId(user.getUserId())
				.build();
		
		// TODO pageList��
		List<Comment> list = commentService.getCommentList(commentParam);
		
//		list.stream()
//			.filter(comment -> Code.COMMENT_SECRET_TYPE_PRIVATE.compare(comment.getCode()))
//			.filter(comment -> !user.isUserId(comment.getUserId()))
//			.filter(comment -> !user.isUserId(articleUserId))
//			.forEach(comment -> comment.setContents("��� ����Դϴ�."));
		
//		list.stream()
//			.filter(comment -> /* TODO !UserGrade.SUPER_USER.compare(user.getGrade()) && */ Code.COMMENT_SECRET_TYPE_REPORTED.compare(comment.getCode()))
//			.forEach(comment -> comment.setContents("�Ű� ������ ����Դϴ�."));
		
		List<CommentDto> commentList = list.stream()
				.map(Comment::showContent)
				.collect(Collectors.toList());
		
		return commentList;
	}
}
