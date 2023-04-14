import Navbar from './components/UI/Navbar/Navbar';
import JobsList from './components/Jobs/JobsList/JobsList';
import Registration from './components/Auth/Registration/Registration';
import {Routes, Route} from 'react-router-dom'
import Login from './components/Auth/Login/Login';
import { useContext, useEffect } from 'react';
import { Context } from '.';
import {observer} from 'mobx-react-lite'
import AddJob from './components/Jobs/AddJob/AddJob';

function App() {

  const {store} = useContext(Context);
  useEffect(() => {
    if (localStorage.getItem('token')) {
      store.checkAuth();
    }
  })

  return (
    <div className="App">
      <Navbar/>
      <Routes>
        <Route path='/' element={<JobsList/>} />
        <Route path='/login' element={<Login/>} />
        <Route path='/registration' element={<Registration/>} />
        <Route path='/addJob' element={<AddJob/>} />
      </Routes>
    </div>
  );
}

export default observer(App);
