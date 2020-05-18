INSERT INTO `users` (`id`, `email`, `is_moderator`, `name`, `password`, `reg_time`) VALUES ('1', 'test@mail.ru', '1', 'testName', '123', '2018-07-16 17:35');
INSERT INTO `posts` (`id`, `is_active`, `moderation_id`, `moderation_status`, `text`, `time`, `title`, `view_count`, `user_id`) VALUES ('1', '1', '1', 'ACCEPTED', 'text', ' 2018-07-16 17:35', 'title', '0', '1');
INSERT INTO `posts` (`id`, `is_active`, `moderation_id`, `moderation_status`, `text`, `time`, `title`, `view_count`, `user_id`) VALUES ('2', '1', '1', 'ACCEPTED', 'NEW', ' 2018-07-16 17:35', 'title', '0', '1');

