import {CrystalAdmin, fetchAdminConfiguration} from './crystalAdmin'
import {customizeDataProvider, getApplicationViews} from './application'

const configuration = await fetchAdminConfiguration('http://localhost:8080');
customizeDataProvider(configuration.dataProvider);
const applicationViews = getApplicationViews();

export const App = () => (
	<CrystalAdmin configuration = {configuration} applicationViews = {applicationViews} />
);
