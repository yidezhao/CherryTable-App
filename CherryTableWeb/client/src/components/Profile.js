import React from 'react';
import '../style/Profile.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import PageNavbar from './PageNavbar';
import ProfileArea from '../components/ProfileArea';
import cookie from 'react-cookies'
import FileUpload from '../components/UploadImage'
import FileUploadGallery from '../components/UploadImageGallery'

export default class Profile extends React.Component {
    
    constructor(props) {
        super(props);

        // The state maintained by this React Component. 
        this.state = {
            name: cookie.load("username"),
            email: "",
            address: "",
            phone: "",
            description: "",
            category: "",
            description: "",
            profilePicUrl: "",
            galleryPics: []
        }

        this.updateState = this.updateState.bind(this);
    }

    fetchOrgDetails(values, callback) {
        var reqUrl = 'http://localhost:8080/fetchOrgDetails?name=' + values.name;
        console.log(reqUrl);

        fetch(reqUrl).then(res => console.log(res.body))
        var xmlhttp = new XMLHttpRequest();

        xmlhttp.onreadystatechange = function() {
          if (this.readyState == 4 && this.status == 200) {
            var res = JSON.parse(this.responseText);
            console.log(JSON.stringify(res));    
            callback(res);
          };
        };

        xmlhttp.open('GET', reqUrl, true);
        xmlhttp.send();
    }

    updateState(res) {
        this.setState({name: res.org.name, email: res.org.email, address: res.org.address, phone: res.org.contact,
        category: res.org.category, description: res.org.description, profilePicUrl: res.org.profilePic, 
        galleryPics: res.org.galleryUrls});
        console.log(this.state);
    }

    // React function that is called when the page loads.
    componentDidMount() {
      // temp fix
        if (this.state.name === "undefined") {
          cookie.save("username", "test_org_14");
        }

        var currName = cookie.load('username');
        this.fetchOrgDetails({name: currName}, this.updateState);
    }

    render() {
        return (
          <div className="Profile">
    
            <PageNavbar active="profile" />
    
            <br></br>
            <div className="container profile-container">
              <div className="jumbotron">
                <div className="profile-container">
                <img src={this.state.profilePicUrl} alt="Profile Picture"></img>
                  <div className="profile-header">
                    <div className="header"><strong>Organization Name:</strong> {this.state.name}</div>
                    <div className="header"><strong>Email Address:</strong> {this.state.email}</div>
                    <div className="header"><strong>Phone Number:</strong> {this.state.phone}</div>
                    <div className="header"><strong>Physical Address: </strong>{this.state.address}</div>
                    <div className="header"><strong>Category: </strong>{this.state.category}</div>
                    <div className="header"><strong>Description: </strong>{this.state.description}</div>
                    <a href="/MyDonors">View my donors</a>
                    <div>
                      {this.state.galleryPics.map((e, idx) => 
                        <img src={e} alt={"Gallery Photo" + idx}></img>
                      )}
                  </div>
                <div className = "gallery">
                  </div>
                  </div>
                </div>
              </div>
              
              <ProfileArea expand={false} />
              <br></br>
              <p>New profile photo:</p>
              <FileUpload/>
              <br></br>
              <p>Upload to gallery:</p>
              <FileUploadGallery/>
                {/* {this.state.galleryPics.map(function(url) {
                    <img src={url} alt="Profile Picture"></img>;
                })} */}
            </div>
          </div>
        );
      }
}