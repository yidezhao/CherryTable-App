import React, {Component} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import cookie from 'react-cookies'

class FileUpload extends Component {
  constructor () {
    super();
    this.state = {
      file: null,
      test: null
    };

    this.handleFileUpload = this.handleFileUpload.bind(this);
    this.submitFile = this.submitFile.bind(this);
  }

  submitFile() {
    const formData = new FormData();
    formData.append('pic', this.state.file);
    formData.append('name', cookie.load('username'));
    var xmlhttp = new XMLHttpRequest();
    
    xmlhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
          var res = JSON.parse(this.responseText);
          console.log(JSON.stringify(res));    
          var delayInMilliseconds = 10000; //1 second

            setTimeout(function() {
            //your code to be executed after 1 second
            }, delayInMilliseconds);

        };
      };

      xmlhttp.open("POST", 'http://localhost:8080/' + 'uploadImage');
      xmlhttp.send(formData);
  }

  handleFileUpload(event) {
      console.log(event.target.files[0]);
      this.setState({ file: event.target.files[0] }, () => {
        console.log(this.state.file);
      });
  }

  render () {
    return (
      <form onSubmit={this.submitFile}>
        <input label='upload file' type='file' onChange={this.handleFileUpload} />
        <button type='submit'>Send</button>
      </form>
    );
  }
}

export default FileUpload;