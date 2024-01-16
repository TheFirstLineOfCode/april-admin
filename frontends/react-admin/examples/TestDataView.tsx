import {useEffect, useState} from 'react';
import Button from '@mui/material/Button';
import {fetchUtils, useTranslate, Title} from 'react-admin'
import {useDataProvider, useNotify} from 'react-admin';
import {notifyFetchError} from './crystalAdmin'

const TOTALS_NOT_FETCHED = -1;
const TOTALS_FETCHED = -2;
const FAILED_TO_FETCH_TOTALS = -3;
const FAILED_TO_LOAD_TEST_DATA = -4;
const FAILED_TO_CLEAR_TEST_DATA = -5;
const LOADING_TEST_DATA = -6;
const CLEARING_TEST_DATA = -7;

export const TestDataView = () => {
	const translate = useTranslate();
	const [state, setState] = useState(TOTALS_NOT_FETCHED);
	const [totalUsers, setTotalUsers] = useState(TOTALS_NOT_FETCHED);
	const [totalPosts, setTotalPosts] = useState(TOTALS_NOT_FETCHED);
	const dataProvider = useDataProvider();
	const notify = useNotify();
	
	const fetchTotals = () => {
		httpClient(`${serviceUrl}/test-data/totals`).
			then(({json}) => {
				setState(TOTALS_FETCHED);
				setTotalUsers(json.total_users);
				setTotalPosts(json.total_posts);
			}).catch(error => {
				notifyFetchError(notify, error);
				setState(FAILED_TO_FETCH_TOTALS);
			});
	}
	
	const loadTestData = () => {
		if (!window.confirm("Are you sure to load test data?"))
			return;
		
		setState(LOADING_TEST_DATA);
		
		dataProvider.loadTestData().then(({data}) => {
			setState(TOTALS_FETCHED);
			setTotalUsers(data.total_users);
			setTotalPosts(data.total_posts);
		}).catch(error => {
			notifyFetchError(notify, error);
			setState(FAILED_TO_LOAD_TEST_DATA);
		});
	}
	
	const clearTestData = () => {
		if (!window.confirm("Are you sure to clear test data?"))
			return;
		
		setState(CLEARING_TEST_DATA);
		
		dataProvider.clearTestData().then(({data}) => {
			setState(TOTALS_FETCHED);
			setTotalUsers(data.total_users);
			setTotalPosts(data.total_posts);
		}).catch(error => {
			notifyFetchError(notify, error);
			setState(FAILED_TO_CLEAR_TEST_DATA);
		});
	}
	
	useEffect(() => {
		if (state == TOTALS_NOT_FETCHED) {
			fetchTotals();
		}
	}, [state]);
	
	if (state == TOTALS_NOT_FETCHED) {
		return (
			<>
				<Title title="ca.title.testData" />
				<strong>{translate('TestDataView.totalUsers')}: ?.</strong>
				<br />
				<strong>{translate('TestDataView.totalPosts')}: ?.</strong>
				<Button variant="contained" size="medium"
						sx= {{width: 256, padding: 1, margin: 2}}
							disabled>
					{translate('TestDataView.loadTestData')}
				</Button>
			</>
		);
	} else if (state == FAILED_TO_FETCH_TOTALS) {
		return (<strong>Error. Failed to fetch totals.</strong>);
	} else if (state == FAILED_TO_LOAD_TEST_DATA) {
		return (<strong>Error. Failed to load test data.</strong>);
	} else if (state == FAILED_TO_CLEAR_TEST_DATA) {
		return (<strong>Error. Failed to clear test data.</strong>);
	} else if (state == LOADING_TEST_DATA) {
		return (
			<>
				<Title title="ca.title.testData" />
				<strong>{translate('TestDataView.totalUsers')}: ?.</strong>
				<br />
				<strong>{translate('TestDataView.totalPosts')}: ?.</strong>
				<Button variant="contained" size="medium"
						sx= {{width: 256, padding: 1, margin: 2}}
							disabled>
					{translate('TestDataView.loadingTestData')}
				</Button>
			</>
		);
	} else if (state == CLEARING_TEST_DATA) {
		return (
			<>
				<Title title="ca.title.testData" />
				<strong>{translate('TestDataView.totalUsers')}: ?.</strong>
				<br />
				<strong>{translate('TestDataView.totalPosts')}: ?.</strong>
				<Button variant="contained" size="medium"
						sx= {{width: 256, padding: 1, margin: 2}}
							disabled>
					{translate('TestDataView.clearingTestData')}
				</Button>
			</>
		);
	} else if (TOTALS_FETCHED && totalUsers == 0 && totalPosts == 0) {
		return (
			<>
				<Title title="ca.title.testData" />
				<strong>{translate('TestDataView.totalUsers')}: {totalUsers}.</strong>
				<strong>{translate('TestDataView.totalPosts')}: {totalPosts}.</strong>
				<Button variant="contained" size="medium"
						sx= {{width: 256, padding: 1, margin: 2}}
							onClick={loadTestData}>
					{translate('TestDataView.loadTestData')}
				</Button>
			</>
		);
	} else {
		return (
			<>
				<Title title="ca.title.testData" />
				<strong>{translate('TestDataView.totalUsers')}: {totalUsers}.</strong>
				<strong>{translate('TestDataView.totalPosts')}: {totalPosts}.</strong>
				<Button variant="contained" size="medium"
						sx= {{width: 256, padding: 1, margin: 2}}
							onClick={clearTestData}>
					{translate('TestDataView.clearTestData')}
				</Button>
			</>
		);
	}
}
