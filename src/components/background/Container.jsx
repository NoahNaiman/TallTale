import React, {Component} from 'react';

const defaultStyle = {
    marginTop: '10px',
};

export default class Container extends Component {
    render = () => {
        return (
            <>
                <div className="container" style={this.props.style ? this.props.style : defaultStyle}>
                    <div className={`notification ${this.props.className ? this.props.className : 'is-primary'}`}>
                        { this.props.children }
                    </div>
                </div>
            </>
        );
    };
}
