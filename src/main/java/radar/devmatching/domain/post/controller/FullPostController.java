package radar.devmatching.domain.post.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import radar.devmatching.domain.comment.service.CommentService;
import radar.devmatching.domain.comment.service.dto.MainCommentDto;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.post.service.PostService;
import radar.devmatching.domain.post.service.dto.PresentFullPostDto;
import radar.devmatching.domain.post.service.dto.UpdatePostDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("posts")
public class FullPostController {

	private final PostService postService;
	private final CommentService commentService;

	@GetMapping("/{simplePostId}")
	public String getFullPost(@PathVariable long simplePostId, Model model) {
		SimplePost findPost = postService.getPost(simplePostId);

		// TODO: simplePostId에 해당하는 apply 중 state가 수락인 apply count() 반환
		// int applyCount = applyService.getApplicationNum(simplePostId);

		// if (findPost.getWriter().getId() != user.getId())
		//	TODO: simplePostId에 해당하는 apply 중 userID가 같은 applyList 반환(게시글에 들어가면 내가 신청한 신청 현환을 볼 수 있다)
		//	List<신청DTO> list = applyService.get~~(simplePostID, userId);

		List<MainCommentDto> allComments = commentService.getAllComments(findPost.getFullPost().getId());

		model.addAttribute("PresentFullPostDto", PresentFullPostDto.of(findPost));
		// model.addAttribute("isWriter", findPost.getWriter() == user.getId())
		// model.addAttribute("applyCount", applyCount);
		// model.addAttribute("신청DTO", list);
		model.addAttribute("allComments", allComments);
		return "post/post";
	}

	@GetMapping("/{simplePostId}/edit")
	public String getUpdatePost(@PathVariable long simplePostId, Model model) {
		SimplePost findPost = postService.getPost(simplePostId);

		model.addAttribute("UpdatePostDto", UpdatePostDto.of(findPost));
		return "post/editPost";
	}

	@PostMapping("/{simplePostId}/edit")
	public String updatePost(@PathVariable long simplePostId, @ModelAttribute UpdatePostDto updatePostDto) {
		postService.updatePost(simplePostId, updatePostDto);

		return "post/post";
	}

	@PostMapping("/{simplePostId}/delete")
	public String deletePost(@PathVariable long simplePostId) {
		return "post/myPosts";
	}

	@GetMapping("/{simplePostId}/end")
	public String closePost(@PathVariable long simplePostId) {
		return "post/post";
	}

}
