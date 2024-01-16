import {TranslationMessages} from 'ra-core';
import chineseMessages from 'ra-language-chinese';

export default {
	...chineseMessages,
	ca: {
		menu: {
			tools: '工具',
			help: '帮助'			
		},
		title: {
			users: '用户',
			posts: '帖子',
			testData: '测试数据',
			about: '关于'
		}
	},
	AboutView: {
		about: '关于',
		applicationName: '应用名',
		version: '版本',
		developer: '开发者',
		close: '关闭'
	},
	TestDataView: {
		totalUsers: '总用户数',
		totalPosts: '总贴子数',
		loadTestData: '装载测试数据',
		clearTestData: '清除测试数据',
		loadingTestData: '装载中....',
		cleaningTestData: '清除中....'
	}
}
