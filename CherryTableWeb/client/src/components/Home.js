import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import PageNavbar from './PageNavbar';
import { Redirect } from 'react-router-dom';

export default class Home extends React.Component {
    constructor(props) {
        super(props);

        // The state maintained by this React Component. This component maintains the list of genres,
        // and a list of movies for a specified genre.
        this.state = {
            requests: []
        }

    }

    // React function that is called when the page load.
    componentDidMount() {

    }


    render() {
        if (this.props.location.state) {
            return (
            < div >

                <PageNavbar active="home" />
                <div>
                    <div className="h5">Welcome to Cherry Table</div>
                    <div>
                        {this.state.requests}
                        <p>You are at home, {this.props.location.state.username}</p>
                    </div>
                </div>
            </div >
        );}
        else{
            return(<Redirect to="/"/>);
        }
    }
}