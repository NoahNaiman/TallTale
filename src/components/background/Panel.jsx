import React, {Component} from 'react';

export default class Panel extends Component {
    render = () => {
        return (
            <>
                <article className={`tile is-child ${this.props.className}`}>
                    { this.props.children }
                </article>
            </>
        );
    };
}
