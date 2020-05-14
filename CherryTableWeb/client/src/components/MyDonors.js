import React from 'react';
import '../style/Donor.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import PageNavbar from './PageNavbar';
import cookie from 'react-cookies'
import DonorIndividual from './DonorIndividual';

export default class MyDonors extends React.Component {
    
    constructor(props) {
        super(props);

        // The state maintained by this React Component. 
        this.state = {
          donors: []
        }
    }

    // React function that is called when the page loads.
    componentDidMount() {
      var currName = cookie.load('username');
      var reqUrl = 'http://localhost:8080/fetchDonorsForOrg?name=' + currName;

      console.log(reqUrl);
    
      fetch(reqUrl, { method: 'GET'}).then(res => { return res.json();}, err => { 
        console.log(err); })
        .then (donors => {
          console.log(donors);

          var donorArr = donors.donorArr;

          if (!donorArr) return;
          
          let donorDivs = donorArr.map((donorObj, i) =>
            <DonorIndividual 
              key = {i}
              donorName={donorObj[0]}
              email={donorObj[2]}
              amount={donorObj[1]}
              />
          );

      this.setState({
        donors: donorDivs
      });
    }, err => {
      console.log(err);
    });
  }

    render() {
      return (
        <div className="Donors">
  
          <PageNavbar active="profile" />
  
          <br></br>
          <div className="container donor-container">
            <br></br>
            <div className="jumbotron">
              <div className="donor-container">
                <div className="donor-header">
                  <div className="header"><strong>Donor Name</strong></div>
                  <div className="header"><strong>Donor Email</strong></div>
                  <div className="header"><strong>Total Donations ($)</strong></div>
                </div>
                <div className="donor-container" id="results">
                  {this.state.donors}
                </div>
              </div>
            </div>
          </div>
        </div>
      );
    }
}