import React, {Component} from 'react';

export default class Preview extends Component {
    render = () => {
        return (
            <>
                <textarea
                    className={`textarea has-fixed-size has-background-primary has-text-light is-light`}
                    rows="27"
                    value={this.props.text}
                    readOnly/>
            </>
        );
    };
}
