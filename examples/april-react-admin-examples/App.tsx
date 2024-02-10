import {AprilAdmin, fetchAdminConfiguration} from 'april-react-admin'
import PolyglotI18nProvider from 'ra-i18n-polyglot'
import en from './language-english';
import cn from './language-chinese';
import chineseMessages from './language-chinese'
import {customizeDataProvider, getApplicationViews} from './application'

const I18nProvider = PolyglotI18nProvider(
	locale => {
		if (locale === 'en') {
			return import('./language-english').then(messages => messages.default);
		}
		
		return chineseMessages;
	},
	'cn',
	[
		{ locale: 'cn', name: '中文' },
		{ locale: 'en', name: 'English' }
	],
	{allowMissing: true}
);

const configuration = await fetchAdminConfiguration('http://localhost:8080');
customizeDataProvider(configuration.dataProvider);

export const App = () => (
	<AprilAdmin configuration = {configuration} applicationViews = {getApplicationViews()} i18nProvider = {I18nProvider} />
);
