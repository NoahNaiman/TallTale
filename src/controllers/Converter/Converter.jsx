import React, {Component} from 'react';
import LoadingBar from "./LoadingBar";
import Poller from "./Poller";

const defaultStyle = {
    marginBottom: '10px',
    marginRight: '10px'
};

export default class Converter extends Component {
    constructor(props) {
        super(props);

        this.state = {
            polling: false,
            value: 0,
            downloadAvailable: false,
        };

        this.value = 0;
    }

    clickHandler = (e) => {
        e.preventDefault();
        if (this.props.text === '') return;
        this.setState({
            polling: true,
        })
    };

    valueHandler = (value) => {
        this.setState({
            value,
        });
    };

    render = () => {
        return (
            <>
                <button
                    className="button is-link"
                    style={defaultStyle}
                    onClick={this.clickHandler}>
                    Generate Sound Clip
                </button>
                { (this.state.polling && this.state.value !== 100) && <Poller text={this.props.text} update={this.valueHandler}/>}
                { this.state.value === '100' && (
                    <a
                        className="button is-success"
                        href="http://talltale.net:8080/temp.wav"
                        download="item.wav"
                        // onClick={() => this.setState({downloadAvailable: false})}
                    >
                        Download Sound Clip
                    </a>
                )}
                <LoadingBar value={this.state.value}/>
            </>
        );
    };
}
